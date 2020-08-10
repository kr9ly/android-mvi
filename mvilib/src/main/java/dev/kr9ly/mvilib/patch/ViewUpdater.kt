package dev.kr9ly.mvilib.patch

class ViewUpdater<T>(
    @PublishedApi
    internal val layout: T,
    @PublishedApi
    internal val diffDetector: DiffDetector
) {

    @PublishedApi
    internal var initialBinding = true

    internal fun doneInitialPatch() {
        initialBinding = false
    }

    inline fun init(executor: (T) -> Unit) {
        if (initialBinding) {
            executor(layout)
        }
    }

    inline fun <V> patch(value1: V, executor: (T, V) -> Unit) {
        if (diffDetector.detect(value1)) {
            executor(layout, value1)
        }
    }

    inline fun <V1, V2> patch(value1: V1, value2: V2, executor: (T, V1, V2) -> Unit) {
        var result = diffDetector.detect(value1)
        result = diffDetector.detect(value2) || result
        if (result) {
            executor(layout, value1, value2)
        }
    }

    inline fun <V1, V2, V3> patch(
        value1: V1,
        value2: V2,
        value3: V3,
        executor: (T, V1, V2, V3) -> Unit
    ) {
        var result = diffDetector.detect(value1)
        result = diffDetector.detect(value2) || result
        result = diffDetector.detect(value3) || result
        if (result) {
            executor(layout, value1, value2, value3)
        }
    }

    inline fun <V1, V2, V3, V4> patch(
        value1: V1,
        value2: V2,
        value3: V3,
        value4: V4,
        executor: (T, V1, V2, V3, V4) -> Unit
    ) {
        var result = diffDetector.detect(value1)
        result = diffDetector.detect(value2) || result
        result = diffDetector.detect(value3) || result
        result = diffDetector.detect(value4) || result
        if (result) {
            executor(layout, value1, value2, value3, value4)
        }
    }

    inline fun <V1, V2, V3, V4, V5> patch(
        value1: V1,
        value2: V2,
        value3: V3,
        value4: V4,
        value5: V5,
        executor: (T, V1, V2, V3, V4, V5) -> Unit
    ) {
        var result = diffDetector.detect(value1)
        result = diffDetector.detect(value2) || result
        result = diffDetector.detect(value3) || result
        result = diffDetector.detect(value4) || result
        result = diffDetector.detect(value5) || result
        if (result) {
            executor(layout, value1, value2, value3, value4, value5)
        }
    }

    inline fun <V1, V2, V3, V4, V5, V6> patch(
        value1: V1,
        value2: V2,
        value3: V3,
        value4: V4,
        value5: V5,
        value6: V6,
        executor: (T, V1, V2, V3, V4, V5, V6) -> Unit
    ) {
        var result = diffDetector.detect(value1)
        result = diffDetector.detect(value2) || result
        result = diffDetector.detect(value3) || result
        result = diffDetector.detect(value4) || result
        result = diffDetector.detect(value5) || result
        result = diffDetector.detect(value6) || result
        if (result) {
            executor(layout, value1, value2, value3, value4, value5, value6)
        }
    }
}