package com.poseidonapp.viewmodel.pendingrequest

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.poseidonapp.model.pendingrequest.PendingRequestResponse
import com.poseidonapp.retrofit.ApiClient
import com.poseidonapp.retrofit.ResponseHandler
import com.poseidonapp.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class PendingRequestViewModel: BaseViewModel() {

    var pendingrequestSuccess= MutableLiveData<PendingRequestResponse>()

    fun pendingrequestRequest(sessionToken:String){
        viewModelScope.launch {
            isLoading.value=true
            try {
                val apiClient= ApiClient.getApiClient()
                val response= ResponseHandler().handleSuccess(
                    apiClient!!.pendingRequest(sessionToken)
                )
                isLoading.value=false
                val data= response.data

                if(data?.status == true){
                    pendingrequestSuccess.value = data.let { it }
                }else{
                    apiError.value = data?.message
                }
            }catch (e: Exception) {
                var exception = ResponseHandler().handleException<String>(e)
                Log.e("@@@", e.toString())

                if (e.toString().contains("401")){
                    apiError.value = "401"
                }else {
                    var exception = ResponseHandler().handleException<String>(e)
                    apiError.value = exception.message.toString()
                }
                isLoading.value = false

            }
        }
    }
}