package dev.kr9ly.mvilib.arch.component

import androidx.viewbinding.ViewBinding
import dev.kr9ly.mvilib.arch.provider.DependencyProvider
import dev.kr9ly.mvilib.component.StatefulComponent

interface StatefulComponentProvider<AppDependencyProvider : DependencyProvider<AppDependencyProvider>, Layout : ViewBinding, State> {

    fun provide(dependencyProvider: AppDependencyProvider, parent: StatefulComponent<AppDependencyProvider, *, *>?): StatefulComponent<AppDependencyProvider, Layout, State>

    fun createInitialState(): State
}