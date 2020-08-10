package dev.kr9ly.mvilib.arch.mvi.stateful

import androidx.viewbinding.ViewBinding
import dev.kr9ly.mvilib.action.ActionDispatcher
import dev.kr9ly.mvilib.lifecycle.LifecycleEvents

interface MviIntent<Layout : ViewBinding> {

    fun intent(
        layout: Layout,
        events: LifecycleEvents,
        dispatcher: ActionDispatcher
    )
}