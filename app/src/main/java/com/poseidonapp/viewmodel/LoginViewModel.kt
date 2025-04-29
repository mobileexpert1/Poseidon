package com.poseidonapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.poseidonapp.model.login.LoginResponse
import com.poseidonapp.retrofit.ApiClient
import com.poseidonapp.retrofit.ResponseHandler
import com.poseidonapp.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class LoginViewModel : BaseViewModel(){


        var loginSuccess = MutableLiveData<LoginResponse>()

        fun loginRequest(email : String, password : String,device_token:String) {

            viewModelScope.launch {
                isLoading.value = true

                try {
                    val apiClient = ApiClient.getApiClient()!!
                    val response = ResponseHandler().handleSuccess(
                        apiClient.loginRequest(
                            email, password,device_token
                        )
                    )
                    isLoading.value = false

                    val data= response.data?.body()

                    if(data?.status == true){
                        loginSuccess.value = response.data.body()
                    }else{
                        apiError.value = data?.message
                    }

                } catch (e: Exception) {
//                    var obj = JSONObject((e as HttpException).response()!!.errorBody()!!.string())
//                    apiError.value = obj.getString("message")
//                    isLoading.value = false
                    var exception = ResponseHandler().handleException<String>(e)
//                      apiError.value = exception.message.toString()
                    isLoading.value = false
                }
            }
        }





   /* fun isValidLogin(): Boolean {
        when {
            email.value.isNullOrEmpty() -> getNavigator()?.showError(
                getApplication<BaseApp>().getString(
                    R.string.please_enter_email_address

                )
            )
            !isValidMail(email.value.toString().trim()) -> getNavigator()?.showError(
                getApplication<BaseApp>().getString(R.string.plz_enter_valid_email_address)
            )
            password.value.isNullOrEmpty() -> getNavigator()?.showError(
                getApplication<BaseApp>().getString(
                    R.string.please_enter_password

                )
            )
            else -> return true
        }
        return false
    }*/

}