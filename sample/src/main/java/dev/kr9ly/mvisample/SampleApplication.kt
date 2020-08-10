package dev.kr9ly.mvisample

import android.app.Application
import toothpick.Scope
import toothpick.ktp.KTP

class SampleApplication : Application() {

    lateinit var scope: Scope

    override fun onCreate() {
        super.onCreate()

        scope = KTP.openRootScope()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)

        scope.release()
    }
}