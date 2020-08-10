package dev.kr9ly.mvilib.component

import android.view.View
import androidx.viewbinding.ViewBinding
import dev.kr9ly.mvilib.arch.component.StatelessComponentProvider
import dev.kr9ly.mvilib.arch.provider.DependencyProvider
import java.util.*

open class StatelessComponentManager<AppDependencyProvider : DependencyProvider<AppDependencyProvider>> internal constructor(
    private val dependencyProvider: AppDependencyProvider
) {

    private val viewComponents = WeakHashMap<View, StatelessComponent<AppDependencyProvider, *, *>>()

    @Suppress("UNCHECKED_CAST")
    fun <Layout : ViewBinding, Argument> bind(
        layout: Layout,
        provider: StatelessComponentProvider<AppDependencyProvider, Layout, Argument>,
        argument: Argument
    ): StatelessComponent<AppDependencyProvider, Layout, Argument> {
        val prevComponent = viewComponents[layout.root] as? StatelessComponent<AppDependencyProvider, Layout, Argument>
        if (prevComponent != null) {
            prevComponent.update(argument)
            return prevComponent
        }
        val component = provider.provide(dependencyProvider)
        component.assemble(layout, argument, this)
        viewComponents[layout.root] = component
        return component
    }
}