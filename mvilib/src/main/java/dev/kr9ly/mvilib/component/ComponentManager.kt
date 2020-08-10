package dev.kr9ly.mvilib.component

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.transition.Scene
import android.transition.TransitionManager
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.viewbinding.ViewBinding
import dev.kr9ly.mvilib.arch.component.StatefulComponentProvider
import dev.kr9ly.mvilib.arch.component.StatelessComponentProvider
import dev.kr9ly.mvilib.arch.provider.DependencyProvider
import dev.kr9ly.mvilib.arch.transition.TransitionProvider
import java.util.*

class ComponentManager<AppDependencyProvider : DependencyProvider<AppDependencyProvider>> internal constructor(
    private val backgroundHandler: Handler,
    private val dependencyProvider: AppDependencyProvider,
    private val viewState: Bundle?,
    private val componentState: Bundle?,
    private val parentComponent: StatefulComponent<AppDependencyProvider, *, *>? = null
) : StatelessComponentManager<AppDependencyProvider>(dependencyProvider) {

    private val components = mutableListOf<StatefulComponent<AppDependencyProvider, *, *>>()

    fun <Layout : ViewBinding, State> setContentView(
        activity: Activity,
        provider: StatefulComponentProvider<AppDependencyProvider, Layout, State>
    ): StatefulComponent<AppDependencyProvider, Layout, State> {
        val component = provider.provide(dependencyProvider, parentComponent)
        component.assemble(
            activity,
            backgroundHandler,
            provider.createInitialState(),
            viewState,
            componentState
        )
        activity.setContentView(component.containerLayout)
        components.add(component)
        return component
    }

    fun <Layout : ViewBinding, State> create(
        context: Context,
        provider: StatefulComponentProvider<AppDependencyProvider, Layout, State>
    ): StatefulComponent<AppDependencyProvider, Layout, State> {
        val component = provider.provide(dependencyProvider, parentComponent)
        component.assemble(
            context,
            backgroundHandler,
            provider.createInitialState(),
            viewState,
            componentState
        )
        return component
    }

    fun <Layout : ViewBinding, State> switch(
        container: ViewGroup,
        provider: StatefulComponentProvider<AppDependencyProvider, Layout, State>,
        transitionProvider: TransitionProvider? = null
    ): StatefulComponent<AppDependencyProvider, Layout, State> {
        val component = create(container.context, provider)
        val currentComponent = if (container.childCount > 0) {
            val child = container[0]
            components.find { c ->
                c.containerLayout == child
            }
        } else {
            null
        }
        when (val transition = transitionProvider?.provide(container.context, currentComponent?.id)) {
            null -> {
                if (currentComponent != null) {
                    container.removeView(currentComponent.containerLayout)
                    components.remove(currentComponent)
                }
                container.addView(component.containerLayout)
            }
            else -> {
                val scene = if (Build.VERSION.SDK_INT >= 21) {
                    Scene(container, component.containerLayout as View)
                } else {
                    @Suppress("DEPRECATION")
                    Scene(container, component.containerLayout)
                }
                TransitionManager.go(scene, transition)
            }
        }
        components.add(component)
        return component
    }

    internal fun save(viewState: Bundle, componentState: Bundle) {
        for (component in components) {
            component.save(viewState, componentState)
        }
    }

    internal fun onStart() {
        for (component in components) {
            component.onStart()
        }
    }

    internal fun onResume() {
        for (component in components) {
            component.onResume()
        }
    }

    internal fun onPause() {
        for (component in components) {
            component.onPause()
        }
    }

    internal fun onStop() {
        for (component in components) {
            component.onStop()
        }
    }

    internal fun onDestroy() {
        for (component in components) {
            component.onDestroy()
        }
    }

    internal fun dispatchKeyEvent(event: KeyEvent): Boolean {
        var consumed = false
        for (component in components) {
            consumed = consumed.or(component.dispatchKeyEvent(event))
        }
        return consumed
    }

    companion object {

        internal fun <AppDependencyProvider : DependencyProvider<AppDependencyProvider>> createRoot(
            dependencyProvider: AppDependencyProvider,
            savedInstanceState: Bundle?
        ): ComponentManager<AppDependencyProvider> {
            val handlerThread = HandlerThread("LazyComponentInitializer")
            handlerThread.start()

            val viewState = savedInstanceState?.getBundle(StatefulComponent.viewStateKey)
            val componentState = savedInstanceState?.getBundle(StatefulComponent.componentStateKey)
            return ComponentManager(
                Handler(handlerThread.looper),
                dependencyProvider,
                viewState,
                componentState
            )
        }
    }
}