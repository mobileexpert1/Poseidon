package com.poseidonapp.snackBar

import androidx.recyclerview.widget.RecyclerView
import android.view.View

/**
 * RecyclerView is a provided dependency, so in order to avoid burdening developers with a
 * potentially unnecessary dependency, we move the RecyclerView-related code here and only call it
 * if we confirm that they've provided it themselves.
 */
internal object RecyclerUtil {
    fun setScrollListener(snackbar: Snackbar, view: View) {
        val recyclerView = view as RecyclerView
        recyclerView.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                snackbar.dismiss()
            }
        })
    }
}
