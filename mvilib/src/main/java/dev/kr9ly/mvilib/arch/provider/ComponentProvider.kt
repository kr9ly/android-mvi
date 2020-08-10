package dev.kr9ly.mvilib.arch.provider

interface ComponentProvider<T> {

    fun provide(): T
}