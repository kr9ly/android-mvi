package dev.kr9ly.mvilib.arch.component

import androidx.viewbinding.ViewBinding
import dev.kr9ly.mvilib.arch.provider.DependencyProvider
import dev.kr9ly.mvilib.component.StatelessComponent

interface StatelessComponentProvider<AppDependencyProvider : DependencyProvider<AppDependencyProvider>, Layout : ViewBinding, Argument> {

    fun provide(dependencyProvider: AppDependencyProvider): StatelessComponent<AppDependencyProvider, Layout, Argument>
}