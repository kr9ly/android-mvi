package dev.kr9ly.mvilib.arch.mvi.stateful

import android.os.Bundle

interface StateSerializer<State> {

    fun serialize(key: String, state: State, bundle: Bundle)

    fun deserialize(key: String, bundle: Bundle): State

    fun serializeList(key: String, stateList: List<State>, bundle: Bundle)

    fun deserializeList(key: String, bundle: Bundle): List<State>
}