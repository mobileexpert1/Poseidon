package com.poseidonapp.ui.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import com.poseidonapp.R
import com.poseidonapp.ui.base.BaseActivity
import com.poseidonapp.utils.Const

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val handler = Handler(Looper.myLooper()!!)
        handler.postDelayed({
            if (sharedPref.getToken(Const.TOKEN)?.length!! > 5) {
                gotMainActivity()
            } else {
                gotoLoginSignUpActivity()
            }
        }, Const.SPLASH_TIMEOUT.toLong())

    }

    private fun initFCM() {

    }

}