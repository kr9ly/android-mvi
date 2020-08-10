package dev.kr9ly.mvilib.arch.mvi.stateful.serializer

import android.os.Bundle
import dev.kr9ly.mvilib.arch.mvi.stateful.StateSerializer
import java.io.Serializable

class SerializableSerializer<T : Serializable> : StateSerializer<T> {

    override fun serialize(key: String, state: T, bundle: Bundle) {
        bundle.putSerializable(key, state)
    }

    @Suppress("UNCHECKED_CAST")
    override fun deserialize(key: String, bundle: Bundle): T =
        bundle.getSerializable(key) as T

    override fun serializeList(key: String, stateList: List<T>, bundle: Bundle) {
        bundle.putSerializable(key, stateList.map { it as Serializable }.toTypedArray())
    }

    @Suppress("UNCHECKED_CAST")
    override fun deserializeList(key: String, bundle: Bundle): List<T> =
        (bundle.getSerializable(key) as Array<Serializable>).map { it as T }
}