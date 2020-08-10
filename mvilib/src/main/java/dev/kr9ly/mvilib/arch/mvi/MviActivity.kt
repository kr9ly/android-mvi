package dev.kr9ly.mvilib.arch.mvi

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import dev.kr9ly.mvilib.arch.component.StatefulComponentProvider
import dev.kr9ly.mvilib.arch.provider.DependencyProvider
import dev.kr9ly.mvilib.component.ComponentManager
import dev.kr9ly.mvilib.component.StatefulComponent

abstract class MviActivity<AppDependencyProvider : DependencyProvider<AppDependencyProvider>, ViewLayout : ViewBinding> : AppCompatActivity() {

    protected abstract fun getDependencyProvider(): AppDependencyProvider

    protected abstract fun createComponentProvider(intent: Intent): StatefulComponentProvider<AppDependencyProvider, ViewLayout, *>

    private lateinit var componentManager: ComponentManager<AppDependencyProvider>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        componentManager = ComponentManager.createRoot(
            getDependencyProvider(),
            savedInstanceState
        )
        componentManager.setContentView(this, createComponentProvider(intent))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val viewState = Bundle()
        val componentState = Bundle()

        componentManager.save(viewState, componentState)

        outState.putBundle(StatefulComponent.viewStateKey, viewState)
        outState.putBundle(StatefulComponent.componentStateKey, componentState)
    }

    override fun onStart() {
        super.onStart()

        componentManager.onStart()
    }

    override fun onResume() {
        super.onResume()

        componentManager.onResume()
    }

    override fun onPause() {
        super.onPause()

        componentManager.onPause()
    }

    override fun onStop() {
        super.onStop()

        componentManager.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()

        componentManager.onDestroy()
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return componentManager.dispatchKeyEvent(event)
    }
}