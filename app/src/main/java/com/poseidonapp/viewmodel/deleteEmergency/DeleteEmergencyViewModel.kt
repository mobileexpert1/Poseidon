package com.poseidonapp.viewmodel.deleteEmergency

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.poseidonapp.model.deleteemergencyrequest.DeletedEmergencyRequestResponse
import com.poseidonapp.retrofit.ApiClient
import com.poseidonapp.retrofit.ResponseHandler
import com.poseidonapp.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class DeleteEmergencyViewModel : BaseViewModel() {

    var deletedEmergencySuccess = MutableLiveData<DeletedEmergencyRequestResponse>()

    fun emergencyRequest(sessionToken:String,serviceId:String){
        viewModelScope.launch {
            isLoading.value=true
            try {
                val apiClient= ApiClient.getApiClient()
                val response= ResponseHandler().handleSuccess(
                    apiClient!!.emergencydeleteRequest(sessionToken, serviceId = serviceId)
                )
                isLoading.value=false
                val data= response.data

                if(data?.status == true){
                    deletedEmergencySuccess.value = data.let { it }
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