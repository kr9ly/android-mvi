package dev.kr9ly.mvilib.arch.provider

import android.content.Context
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

interface ViewLayoutProvider<Layout : ViewBinding> {

    fun provide(context: Context, parent: ViewGroup?): Layout
}