package dev.kr9ly.mvilib.arch.mvi.stateful

import androidx.viewbinding.ViewBinding
import dev.kr9ly.mvilib.arch.provider.DependencyProvider
import dev.kr9ly.mvilib.component.ComponentManager
import dev.kr9ly.mvilib.patch.ViewUpdater

interface MviView<AppDependencyProvider : DependencyProvider<AppDependencyProvider>, Layout : ViewBinding, State> {

    fun view(
        state: State,
        updater: ViewUpdater<Layout>,
        componentManager: ComponentManager<AppDependencyProvider>
    )
}