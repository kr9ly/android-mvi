package dev.kr9ly.mvilib.state

class DiffNotifier<State>(
    private val listener: ((State) -> State) -> Unit
) {

    fun notifyDiff(diff: (State) -> State) {
        listener(diff)
    }
}