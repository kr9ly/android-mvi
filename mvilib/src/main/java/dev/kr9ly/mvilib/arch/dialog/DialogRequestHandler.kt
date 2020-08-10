package dev.kr9ly.mvilib.arch.dialog

import dev.kr9ly.mvilib.arch.provider.DependencyProvider

interface DialogRequestHandler<AppDependencyProvider : DependencyProvider<AppDependencyProvider>> {

    fun process(request: DialogRequest): DialogProvider<AppDependencyProvider>
}