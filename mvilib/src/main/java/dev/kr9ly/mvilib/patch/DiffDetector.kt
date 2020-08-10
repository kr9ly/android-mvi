package dev.kr9ly.mvilib.patch

class DiffDetector {

    private object EndOfState

    private var dirty = false

    private var index = 0

    private var diffBuffer = mutableListOf<Any?>()

    fun startUpdate() {
        dirty = false
        index = 0
    }

    fun detect(value: Any?): Boolean {
        if (dirty) {
            append(value)
            return true
        }
        if (index >= diffBuffer.size) {
            append(value)
            return true
        }
        if (diffBuffer[index] === EndOfState) {
            dirty = true
            append(value)
            return true
        }
        if (value == null || value::class.javaPrimitiveType != null) {
            if (value != diffBuffer[index]) {
                append(value)
                return true
            }
        } else {
            if (value !== diffBuffer[index]) {
                append(value)
                return true
            }
        }
        index++
        return false
    }

    fun finishUpdate() {
        append(EndOfState)
    }

    private fun append(value: Any?) {
        if (index < diffBuffer.size) {
            diffBuffer[index] = value
        } else {
            diffBuffer.add(value)
        }
        index++
    }
}