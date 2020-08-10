package dev.kr9ly.mvilib.dialog

import android.app.Activity
import android.app.Dialog
import android.view.ViewGroup
import dev.kr9ly.mvilib.arch.dialog.DialogProvider
import dev.kr9ly.mvilib.arch.provider.DependencyProvider
import dev.kr9ly.mvilib.component.ComponentManager
import dev.kr9ly.mvilib.component.StatefulComponent

class DialogController<AppDependencyProvider : DependencyProvider<AppDependencyProvider>>(
    private val componentManager: ComponentManager<AppDependencyProvider>,
    private val provider: DialogProvider<AppDependencyProvider>,
    private val onDismissListener: (DialogController<AppDependencyProvider>) -> Unit
) {

    private lateinit var activity: Activity

    private var dialog: Dialog? = null

    private var component: StatefulComponent<AppDependencyProvider, *, *>? = null

    private var detachable = true

    fun setup(activity: Activity) {
        this.activity = activity
    }

    private fun createDialog(activity: Activity) {
        dialog = Dialog(activity, provider.themeResId()).also { dialog ->
            dialog.setOwnerActivity(activity)
            dialog.setOnKeyListener { _, _, keyEvent ->
                false
            }

            dialog.setOnDismissListener {
                component?.onDismiss()
                component?.onPause()
                component?.onStop()
                destroy()
            }

            dialog.setOnCancelListener {
                component?.onCancel()
                component?.onPause()
                component?.onStop()
                destroy()
            }

            component = componentManager.create(activity, provider.provide()).also {
                dialog.setContentView(
                    it.containerLayout,
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
            }
        }
    }

    fun show() {
        if (dialog == null) {
            createDialog(activity)
        }
        dialog?.show()
        component?.onStart()
        component?.onResume()
    }

    fun hide() {
        dialog?.hide()
        component?.onPause()
        component?.onStop()
    }

    fun dismiss() {
        dialog?.dismiss()
    }

    private fun destroy() {
        onDismissListener(this)
        component?.onDestroy()
        dialog = null
        component = null
    }
}