package dev.kr9ly.mvilib.component

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.util.SparseArray
import android.view.KeyEvent
import androidx.viewbinding.ViewBinding
import dev.kr9ly.mvilib.action.ActionDispatcher
import dev.kr9ly.mvilib.arch.dialog.DialogRequest
import dev.kr9ly.mvilib.arch.dialog.DialogRequestHandler
import dev.kr9ly.mvilib.arch.mvi.Action
import dev.kr9ly.mvilib.arch.mvi.stateful.MviIntent
import dev.kr9ly.mvilib.arch.mvi.stateful.MviModel
import dev.kr9ly.mvilib.arch.mvi.stateful.MviView
import dev.kr9ly.mvilib.arch.mvi.stateful.StateSerializer
import dev.kr9ly.mvilib.arch.provider.ComponentProvider
import dev.kr9ly.mvilib.arch.provider.DependencyProvider
import dev.kr9ly.mvilib.arch.provider.ViewLayoutProvider
import dev.kr9ly.mvilib.component.stateful.StatefulComponentLayout
import dev.kr9ly.mvilib.dialog.DialogController
import dev.kr9ly.mvilib.lifecycle.LifecycleEvents
import dev.kr9ly.mvilib.patch.ViewUpdater
import dev.kr9ly.mvilib.patch.DiffDetector
import dev.kr9ly.mvilib.state.StateDispatcher

open class StatefulComponent<AppDependencyProvider : DependencyProvider<AppDependencyProvider>, Layout : ViewBinding, State> internal constructor(
    internal val id: String,
    private val dependencyProvider: DependencyProvider<AppDependencyProvider>,
    private val layoutProvider: ViewLayoutProvider<Layout>,
    private val intentProvider: ComponentProvider<MviIntent<Layout>>,
    private val modelProvider: ComponentProvider<MviModel<State>>,
    private val viewProvider: ComponentProvider<MviView<AppDependencyProvider, Layout, State>>,
    private val stateSerializer: StateSerializer<State>,
    private val dialogRequestHandler: DialogRequestHandler<AppDependencyProvider>,
    private val parent: StatefulComponent<AppDependencyProvider, *, *>? = null
) {

    lateinit var containerLayout: StatefulComponentLayout

    private lateinit var layout: Layout

    private lateinit var mainHandler: Handler

    private lateinit var backgroundHandler: Handler

    private lateinit var diffDetector: DiffDetector

    private lateinit var viewUpdater: ViewUpdater<Layout>

    private lateinit var actionDispatcher: ActionDispatcher

    private lateinit var stateDispatcher: StateDispatcher<State>

    private lateinit var componentManager: ComponentManager<AppDependencyProvider>

    private val lifecycleEvents = LifecycleEvents()

    private val actionQueue = mutableListOf<Action>()

    private var model: MviModel<State>? = null

    private var dialogControllers = mutableListOf<DialogController<AppDependencyProvider>>()

    private var currentDialogRequests = mutableListOf<DialogRequest>()

    private var onStarted = false

    private var onResumed = false

    @Suppress("UNCHECKED_CAST")
    internal fun assemble(
        context: Context,
        backgroundHandler: Handler,
        initialState: State,
        viewState: Bundle?,
        componentState: Bundle?
    ) {
        containerLayout = StatefulComponentLayout(context)

        layout = layoutProvider.provide(context, containerLayout)

        val intent = intentProvider.provide()
        val view = viewProvider.provide()

        componentManager = ComponentManager(
            backgroundHandler,
            dependencyProvider.createChild(),
            viewState,
            componentState,
            this
        )

        actionDispatcher = ActionDispatcher { action ->
            dispatchAction(action)
        }

        mainHandler = Handler(Looper.getMainLooper())
        this.backgroundHandler = backgroundHandler

        diffDetector = DiffDetector()
        viewUpdater = ViewUpdater(layout, diffDetector)
        stateDispatcher = StateDispatcher(initialState, { state ->
            diffDetector.startUpdate()
            view.view(state, viewUpdater, componentManager)
            diffDetector.finishUpdate()
        }, { request ->
            val dialogController = DialogController(componentManager, dialogRequestHandler.process(request)) {
                dialogControllers.remove(it)
                currentDialogRequests.remove(request)
            }
            dialogControllers.add(dialogController)
            dialogController.show()
        })
        intent.intent(layout, lifecycleEvents, actionDispatcher)

        if (viewState == null || componentState == null) {
            diffDetector.startUpdate()
            view.view(initialState, viewUpdater, componentManager)
            diffDetector.finishUpdate()
            viewUpdater.doneInitialPatch()
        } else {
            val state = stateSerializer.deserialize(createLatestStateBundleKey(), componentState)
            val stateHistory = stateSerializer.deserializeList(createStateHistoryBundleKey(), componentState)
            this.currentDialogRequests = (componentState.getSerializable(createDialogRequestBundleKey()) as Array<DialogRequest>).toMutableList()
            stateDispatcher.stateHistory = stateHistory.toMutableList()
            stateDispatcher.currentState = state
            diffDetector.startUpdate()
            view.view(initialState, viewUpdater, componentManager)
            diffDetector.finishUpdate()
            viewUpdater.doneInitialPatch()

            containerLayout.restoreHierarchyState(viewState.getSparseParcelableArray(createBundleKey()))
        }
    }

    internal fun save(viewState: Bundle, componentState: Bundle) {
        val hierarchyState = SparseArray<Parcelable>()
        containerLayout.saveHierarchyState(hierarchyState)
        viewState.putSparseParcelableArray(createBundleKey(), hierarchyState)
        componentState.putSerializable(createDialogRequestBundleKey(), currentDialogRequests.toTypedArray())
        stateSerializer.serialize(createLatestStateBundleKey(), stateDispatcher.currentState, componentState)
        stateSerializer.serializeList(createStateHistoryBundleKey(), stateDispatcher.stateHistory, componentState)
        componentManager.save(viewState, componentState)
    }

    internal fun onStart() {
        onStarted = true
        lifecycleEvents.dispatchOnStart()

        if (model == null) {
            backgroundHandler.post {
                model = modelProvider.provide()
                mainHandler.post {
                    for (action in actionQueue) {
                        dispatchAction(action)
                    }
                }
            }
        }
    }

    internal fun onResume() {
        if (!onStarted) {
            return
        }
        onResumed = true
        lifecycleEvents.dispatchOnResume()
    }

    internal fun onPause() {
        if (!onStarted || !onResumed) {
            return
        }
        onResumed = false
        lifecycleEvents.dispatchOnPause()
    }

    internal fun onStop() {
        if (!onStarted) {
            return
        }
        onStarted = false
        lifecycleEvents.dispatchOnStop()
    }

    internal fun onDestroy() {
        lifecycleEvents.dispatchOnDestroy()
    }

    internal fun onDismiss() {
        lifecycleEvents.dispatchOnDismiss()
    }

    internal fun onCancel() {
        lifecycleEvents.dispatchOnCancel()
    }

    internal fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (componentManager.dispatchKeyEvent(event)) {
            return true
        }

        when (event.keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                if (event.action == KeyEvent.ACTION_UP) {
                    if (stateDispatcher.popHistory()) {
                        return true
                    }
                }
            }
        }

        return false
    }

    private fun createBundleKey(): String {
        if (parent == null) {
            return "component/id:${id}"
        }
        return "${parent.createBundleKey()}/id:${id}"
    }

    private fun createLatestStateBundleKey(): String {
        if (parent == null) {
            return "component/id:${id}:latest"
        }
        return "${parent.createBundleKey()}/id:${id}:latest"
    }

    private fun createStateHistoryBundleKey(): String {
        if (parent == null) {
            return "component/id:${id}:history"
        }
        return "${parent.createBundleKey()}/id:${id}:history"
    }

    private fun createDialogRequestBundleKey(): String {
        if (parent == null) {
            return "component/id:${id}:dialog"
        }
        return "${parent.createBundleKey()}/id:${id}:dialog"
    }

    internal fun dispatchAction(action: Action) {
        val model = this.model
        if (model != null) {
            for (restQueue in actionQueue) {
                dispatchAction(restQueue)
            }
            if (!model.model(action, stateDispatcher.currentState, stateDispatcher)) {
                parent?.dispatchAction(action)
            }
        } else {
            actionQueue.add(action)
        }
    }

    companion object {

        internal const val viewStateKey = "VIEW_STATE"
        internal const val componentStateKey = "CONTAINER_STATE"

        fun <AppDependencyProvider : DependencyProvider<AppDependencyProvider>, Layout : ViewBinding, State> create(
            id: String,
            dependencyProvider: AppDependencyProvider,
            layoutProvider: ViewLayoutProvider<Layout>,
            intentProvider: ComponentProvider<MviIntent<Layout>>,
            modelProvider: ComponentProvider<MviModel<State>>,
            viewProvider: ComponentProvider<MviView<AppDependencyProvider, Layout, State>>,
            stateSerializer: StateSerializer<State>,
            dialogRequestHandler: DialogRequestHandler<AppDependencyProvider>
        ) = StatefulComponent(
            id,
            dependencyProvider,
            layoutProvider,
            intentProvider,
            modelProvider,
            viewProvider,
            stateSerializer,
            dialogRequestHandler
        )
    }
}