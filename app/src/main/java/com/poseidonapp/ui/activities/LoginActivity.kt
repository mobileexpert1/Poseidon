package com.poseidonapp.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.poseidonapp.R
import com.poseidonapp.databinding.ActivityLoginBinding
import com.poseidonapp.ui.base.BaseActivity
import com.poseidonapp.ui.extensions.showHidePassword
import com.poseidonapp.ui.extensions.showHidePasswordDARK
import com.poseidonapp.utils.Const
import com.poseidonapp.utils.HandleClickListener
import com.poseidonapp.viewmodel.LoginViewModel

class LoginActivity : BaseActivity(),HandleClickListener {

    private lateinit var binding: ActivityLoginBinding
    lateinit var viewModel : LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        init()
    }

    private fun init() {
        binding.handleClick = this
        if (sharedPref.getLanguageSelected() == "DARK") {
            binding.passwordET.showHidePasswordDARK()
        }
        if (sharedPref.getLanguageSelected() == "LIGHT") {
            binding.passwordET.showHidePassword()
        }
        binding.passwordET.showHidePassword()
        viewModel= ViewModelProvider(this)[LoginViewModel::class.java]
        observers()
    }

    override fun onViewClick(view: View) {
        when (view.id) {
            R.id.loginTV -> {
                if (binding.emailET.text!!.isEmpty()) {
                    showToast("Please enter email.")
                } /*else if (!isEmailValid(binding.emailET.text.toString())) {
                    showToast("Please enter valid email.")
                } */else if (binding.passwordET.text!!.isEmpty()) {
                    showToast("Please enter password.")
                } else {
                    if(isInternetAvail()) {
                        viewModel.loginRequest(
                            binding.emailET.text.toString(),
                            binding.passwordET.text.toString(),
                            getDeviceToken())
                    }else{
                        showToast(getString(R.string.internet_error))
                    }
                }
//                gotMainActivity()
            }
        }
    }

    private fun  observers(){
        viewModel.loginSuccess.observe(this, Observer {
            it.loginData.apply {
                sharedPref.setToken(Const.TOKEN,it.loginData.token)
                sharedPref.setString(Const.USER_ID,it.loginData.inspectorData.id)
                sharedPref.setString(Const.ROLE,it.loginData.inspectorData.role)
                Log.e("tag","Role...."+it.loginData.inspectorData.role)
            }
            gotMainActivity()
        })

        // login failed
        viewModel.apiError.observe(this) {
            Log.d("tag","ksf ksf : $it")
            showToast(it)
        }

        // loading
        viewModel.isLoading.observe(this) {
            Log.d("tag","ksf ksf isLoading: $it")
            if (it) {
                progressBarPB!!.show()
            } else {
                progressBarPB!!.dismiss()
            }
        }
    }

}