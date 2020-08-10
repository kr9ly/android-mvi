package dev.kr9ly.mvilib.arch.mvi.stateful

import dev.kr9ly.mvilib.arch.mvi.Action
import dev.kr9ly.mvilib.state.StateDispatcher

interface MviModel<State> {

    fun model(
        action: Action,
        state: State,
        dispatcher: StateDispatcher<State>
    ): Boolean
}