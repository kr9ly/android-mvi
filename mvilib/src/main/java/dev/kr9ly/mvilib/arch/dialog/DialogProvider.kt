package dev.kr9ly.mvilib.arch.dialog

import androidx.annotation.StyleRes
import dev.kr9ly.mvilib.arch.component.StatefulComponentProvider
import dev.kr9ly.mvilib.arch.provider.DependencyProvider

interface DialogProvider<AppDependencyProvider : DependencyProvider<AppDependencyProvider>> {

    @StyleRes
    fun themeResId(): Int

    fun provide(): StatefulComponentProvider<AppDependencyProvider, *, *>
}