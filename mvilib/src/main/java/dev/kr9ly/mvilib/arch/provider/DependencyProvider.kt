package dev.kr9ly.mvilib.arch.provider

interface DependencyProvider<AppDependencyProvider : DependencyProvider<AppDependencyProvider>> {

    fun createChild(): AppDependencyProvider
}