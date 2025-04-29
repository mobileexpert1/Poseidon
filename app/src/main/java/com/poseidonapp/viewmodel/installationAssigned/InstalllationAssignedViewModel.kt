package com.poseidonapp.viewmodel.installationAssigned

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.poseidonapp.model.chatNotes.ChatNotesResponse
import com.poseidonapp.model.installationAssigned.InstallationAssignedResponse
import com.poseidonapp.retrofit.ApiClient
import com.poseidonapp.retrofit.ResponseHandler
import com.poseidonapp.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class InstalllationAssignedViewModel:BaseViewModel() {

    var installationAssignedSuccess = MutableLiveData<InstallationAssignedResponse>()

    fun installationAddRequest(sessionToken:String){
        viewModelScope.launch {
            isLoading.value=true
            try {
                val apiClient= ApiClient.getApiClient()
                val response= ResponseHandler().handleSuccess(
                    apiClient!!.installationAssignedRequest(sessionToken)
                )
                isLoading.value=false
                val data= response.data

                if(data?.status == true){
                    installationAssignedSuccess.value = data.let { it }
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