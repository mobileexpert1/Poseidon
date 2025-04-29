package com.poseidonapp.viewmodel.checkroute

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.poseidonapp.model.checkroute.CheckRouteResponse
import com.poseidonapp.retrofit.ApiClient
import com.poseidonapp.retrofit.ResponseHandler
import com.poseidonapp.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class CheckRouteViewModel: BaseViewModel() {

    var checkRouteSuccess = MutableLiveData<CheckRouteResponse>()

    fun checkRouteRequest(sessionToken : String,requestId: String) {

        viewModelScope.launch {
            isLoading.value = true

            try {
                val apiClient = ApiClient.getApiClient()!!
                val response = ResponseHandler().handleSuccess(
                    apiClient.checkRouteRequest(sessionToken,requestId)
                )
                isLoading.value = false
                val data= response.data?.body()
                if(data?.status == true){
                    checkRouteSuccess.value = response.data.body()
                }else{
                    apiError.value = data?.message
                }
            } catch (e: Exception) {
                var exception = ResponseHandler().handleException<String>(e)
                Log.e("@@@", e.toString())
                if (e.toString().contains("401")){
                    apiError.value = "401"
                } else {
                    val exception = ResponseHandler().handleException<String>(e)
                    apiError.value = exception.message.toString()
                }
                isLoading.value = false
            }
        }
    }




}