package dev.kr9ly.mvilib.component.stateful

import android.content.Context
import android.widget.FrameLayout

class StatefulComponentLayout(context: Context) : FrameLayout(context) {

    init {
        isSaveEnabled = false
    }
}