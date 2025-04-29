package com.poseidonapp.snackBar

import android.annotation.TargetApi
import android.graphics.Point
import android.os.Build
import android.view.Display

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
internal class DisplayCompatImplHoneycombMR2 : DisplayCompat.Impl() {
    override fun getSize(display: Display, outSize: Point) {
        display.getSize(outSize)
    }

    override fun getRealSize(display: Display, outSize: Point) {
        display.getSize(outSize)
    }
}
