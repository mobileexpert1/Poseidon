package com.poseidonapp

import android.content.Context

abstract  class AllContext {

    companion object {

        private lateinit var context: Context

        fun setContext(con: Context) {
            context=con
        }
    }
}