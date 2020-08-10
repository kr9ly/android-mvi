package dev.kr9ly.mvilib.arch.mvi.stateless

import dev.kr9ly.mvilib.action.ActionDispatcher

interface ViIntent<Layout> {

    fun intent(
        layout: Layout,
        dispatcher: ActionDispatcher
    )
}