package dev.kr9ly.mvilib.arch.mvi.stateless

import dev.kr9ly.mvilib.arch.provider.DependencyProvider
import dev.kr9ly.mvilib.component.StatelessComponentManager
import dev.kr9ly.mvilib.patch.ViewUpdater

interface ViView<AppDependencyProvider : DependencyProvider<AppDependencyProvider>, Layout, Argument> {

    fun view(
        argument: Argument,
        updater: ViewUpdater<Layout>,
        componentManager: StatelessComponentManager<AppDependencyProvider>
    )
}