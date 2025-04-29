package com.poseidonapp

import android.app.Application
import android.content.Context


class MainApplication : Application() {

    companion object {
         var instance: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        // initialize for any
        instance = this
    }
}