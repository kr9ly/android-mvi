package dev.kr9ly.mvilib.arch.transition

import android.content.Context
import android.transition.Transition

interface TransitionProvider {

    fun provide(context: Context, currentComponentId: String?): Transition?
}