package com.poseidonapp.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


open class BaseViewModel : ViewModel() {

    var apiError= MutableLiveData<String>()
    var isLoading= MutableLiveData<Boolean>()

}