package dev.kr9ly.mvilib.component

import androidx.viewbinding.ViewBinding
import dev.kr9ly.mvilib.action.ActionDispatcher
import dev.kr9ly.mvilib.arch.provider.ComponentProvider
import dev.kr9ly.mvilib.arch.mvi.stateless.ViIntent
import dev.kr9ly.mvilib.arch.mvi.stateless.ViView
import dev.kr9ly.mvilib.arch.provider.DependencyProvider
import dev.kr9ly.mvilib.patch.ViewUpdater
import dev.kr9ly.mvilib.patch.DiffDetector

class StatelessComponent<AppDependencyProvider : DependencyProvider<AppDependencyProvider>, Layout : ViewBinding, Argument> internal constructor(
    private val intentProvider: ComponentProvider<ViIntent<Layout>>,
    private val viewProvider: ComponentProvider<ViView<AppDependencyProvider, Layout, Argument>>,
    private val parent: StatefulComponent<*, *, *>? = null
) {

    lateinit var layout: Layout
        private set

    private lateinit var diffDetector: DiffDetector

    private lateinit var viewUpdater: ViewUpdater<Layout>

    private lateinit var actionDispatcher: ActionDispatcher

    private lateinit var statelessComponentManager: StatelessComponentManager<AppDependencyProvider>

    private lateinit var view: ViView<AppDependencyProvider, Layout, Argument>

    fun assemble(layout: Layout, initialArgument: Argument, statelessComponentManager: StatelessComponentManager<AppDependencyProvider>) {
        this.layout = layout
        layout.root.isSaveEnabled = false
        diffDetector = DiffDetector()
        viewUpdater = ViewUpdater(layout, diffDetector)
        actionDispatcher = ActionDispatcher { action ->
            parent?.dispatchAction(action)
        }
        this.statelessComponentManager = statelessComponentManager
        intentProvider.provide().intent(layout, actionDispatcher)

        view = viewProvider.provide()

        diffDetector.startUpdate()
        view.view(initialArgument, viewUpdater, statelessComponentManager)
        diffDetector.finishUpdate()
        viewUpdater.doneInitialPatch()
    }

    fun update(argument: Argument) {
        diffDetector.startUpdate()
        view.view(argument, viewUpdater, statelessComponentManager)
        diffDetector.finishUpdate()
    }

    companion object {

        fun <AppDependencyProvider : DependencyProvider<AppDependencyProvider>, Layout : ViewBinding, Argument> create(
            intentProvider: ComponentProvider<ViIntent<Layout>>,
            viewProvider: ComponentProvider<ViView<AppDependencyProvider, Layout, Argument>>
        ) = StatelessComponent(
            intentProvider,
            viewProvider
        )
    }
}