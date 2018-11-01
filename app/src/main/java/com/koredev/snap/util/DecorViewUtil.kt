package com.koredev.snap.util

import android.app.Activity
import android.view.View
import androidx.fragment.app.Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private fun Activity.hide() {
    window?.decorView?.systemUiVisibility = (
        View.SYSTEM_UI_FLAG_IMMERSIVE
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN)
}

fun Fragment.hideSystemUi() {
    activity?.run {
        hide()
        window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                GlobalScope.launch(context = Dispatchers.Main) {
                    delay(3000L)
                    hide()
                }
            }
        }
        actionBar?.hide()
    }
}

fun Fragment.showSystemUi() {
    activity?.run {
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.decorView.setOnSystemUiVisibilityChangeListener(null)
        actionBar?.show()
    }
}