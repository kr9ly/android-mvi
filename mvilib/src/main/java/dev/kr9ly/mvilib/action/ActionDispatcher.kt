package dev.kr9ly.mvilib.action

import dev.kr9ly.mvilib.arch.mvi.Action

class ActionDispatcher(
    private val actionDelegate: (Action) -> Unit
) {

    fun dispatch(action: Action) {
        actionDelegate(action)
    }
}