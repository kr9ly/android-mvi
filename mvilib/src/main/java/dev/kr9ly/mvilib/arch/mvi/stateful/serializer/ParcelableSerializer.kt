package dev.kr9ly.mvilib.arch.mvi.stateful.serializer

import android.os.Bundle
import android.os.Parcelable
import dev.kr9ly.mvilib.arch.mvi.stateful.StateSerializer

class ParcelableSerializer<T : Parcelable> : StateSerializer<T> {

    override fun serialize(key: String, state: T, bundle: Bundle) {
        bundle.putParcelable(key, state)
    }

    override fun deserialize(key: String, bundle: Bundle): T =
        requireNotNull(bundle.getParcelable(key))

    override fun serializeList(key: String, stateList: List<T>, bundle: Bundle) {
        bundle.putParcelableArray(key, stateList.map { it as Parcelable }.toTypedArray())
    }

    @Suppress("UNCHECKED_CAST")
    override fun deserializeList(key: String, bundle: Bundle): List<T> =
        requireNotNull(bundle.getParcelableArray(key)?.map { it as T })
}