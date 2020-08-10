package dev.kr9ly.mvilib.lifecycle

class LifecycleEvents {

    private var onStartListeners = mutableListOf<() -> Unit>()

    private var onResumeListeners = mutableListOf<() -> Unit>()

    private var onPauseListeners = mutableListOf<() -> Unit>()

    private var onStopListeners = mutableListOf<() -> Unit>()

    private var onDestroyListeners = mutableListOf<() -> Unit>()

    private var onCancelListeners = mutableListOf<() -> Unit>()

    private var onDismissListeners = mutableListOf<() -> Unit>()

    fun onStart(listener: () -> Unit) {
        onStartListeners.add(listener)
    }

    fun onResume(listener: () -> Unit) {
        onResumeListeners.add(listener)
    }

    fun onPause(listener: () -> Unit) {
        onPauseListeners.add(listener)
    }

    fun onStop(listener: () -> Unit) {
        onStopListeners.add(listener)
    }

    fun onDestroy(listener: () -> Unit) {
        onDestroyListeners.add(listener)
    }

    fun onCancel(listener: () -> Unit) {
        onCancelListeners.add(listener)
    }

    fun onDismiss(listener: () -> Unit) {
        onDismissListeners.add(listener)
    }

    internal fun dispatchOnStart() {
        for (onStartListener in onStartListeners) {
            onStartListener()
        }
    }

    internal fun dispatchOnResume() {
        for (onResumeListener in onResumeListeners) {
            onResumeListener()
        }
    }

    internal fun dispatchOnPause() {
        for (onPauseListener in onPauseListeners) {
            onPauseListener()
        }
    }

    internal fun dispatchOnStop() {
        for (onStopListener in onStopListeners) {
            onStopListener()
        }
    }

    internal fun dispatchOnDestroy() {
        for (onDestroyListener in onDestroyListeners) {
            onDestroyListener()
        }
    }

    internal fun dispatchOnDismiss() {
        for (onDismissListener in onDismissListeners) {
            onDismissListener()
        }
    }

    internal fun dispatchOnCancel() {
        for (onCancelListener in onCancelListeners) {
            onCancelListener()
        }
    }
}