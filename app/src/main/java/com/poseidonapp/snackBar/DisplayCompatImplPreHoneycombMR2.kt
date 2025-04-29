package com.poseidonapp.snackBar

import android.graphics.Point
import android.view.Display

internal class DisplayCompatImplPreHoneycombMR2 : DisplayCompat.Impl() {
    override fun getSize(display: Display, outSize: Point) {
        outSize.x = display.width
        outSize.y = display.height
    }

    override fun getRealSize(display: Display, outSize: Point) {
        outSize.x = display.width
        outSize.y = display.height
    }
}

