package dev.kr9ly.mvilib.state

import android.os.Handler
import android.os.Looper
import dev.kr9ly.mvilib.arch.dialog.DialogRequest

class StateDispatcher<State>(
    internal var currentState: State,
    private val stateListener: (State) -> Unit,
    private val dialogRequestListener: (DialogRequest) -> Unit
) {

    private val mainHandler = Handler(Looper.getMainLooper())

    internal var stateHistory = mutableListOf<State>()

    fun dispatch(diff: (State) -> State) {
        if (Thread.currentThread() == Looper.getMainLooper().thread) {
            val state = diff(currentState)
            currentState = state
            stateListener(state)
        } else {
            mainHandler.post {
                val state = diff(currentState)
                currentState = state
                stateListener(state)
            }
        }
    }

    fun dispatchPush(diff: (State) -> State) {
        if (Thread.currentThread() == Looper.getMainLooper().thread) {
            stateHistory.add(currentState)
            val state = diff(currentState)
            currentState = state
            stateListener(state)
        } else {
            mainHandler.post {
                stateHistory.add(currentState)
                val state = diff(currentState)
                currentState = state
                stateListener(state)
            }
        }
    }

    fun dispatch(update: ApplicationStateUpdate) {
        if (Thread.currentThread() == Looper.getMainLooper().thread) {
            update.dispatch()
        } else {
            mainHandler.post {
                update.dispatch()
            }
        }
    }

    fun dispatch(dialogRequest: DialogRequest) {
        dialogRequestListener(dialogRequest)
    }

    internal fun popHistory(): Boolean {
        if (stateHistory.isEmpty()) {
            return false
        }
        val state = stateHistory.removeAt(stateHistory.size - 1)
        currentState = state
        stateListener(state)
        return true
    }
}