package com.poseidonapp.ui.base

open class BaseData<T> {
    var status: Boolean? = null
    var message: String? = null
    var data: T? = null
    var error :Error?  = null
}