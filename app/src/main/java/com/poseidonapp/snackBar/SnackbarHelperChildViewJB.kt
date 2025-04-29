package com.poseidonapp.snackBar

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.view.View
import com.poseidonapp.ui.extensions.visibleView

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
internal class SnackbarHelperChildViewJB(context: Context) : View(context) {
    init {
        isSaveEnabled = false
        setWillNotDraw(true)
        visibleView(false)
    }

    override fun onWindowSystemUiVisibilityChanged(visible: Int) {
        super.onWindowSystemUiVisibilityChanged(visible)

        val parent = parent
        if (parent is Snackbar) {
            parent.dispatchOnWindowSystemUiVisibilityChangedCompat(visible)
        }
    }
}
