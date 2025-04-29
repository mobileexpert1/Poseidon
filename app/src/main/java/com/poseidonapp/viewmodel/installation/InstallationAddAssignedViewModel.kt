package com.poseidonapp.viewmodel.installation

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.poseidonapp.model.installationAddAssigned.InstallationAddAssignedResponse
import com.poseidonapp.retrofit.ApiClient
import com.poseidonapp.retrofit.ResponseHandler
import com.poseidonapp.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class InstallationAddAssignedViewModel : BaseViewModel() {

    var installationSuccess = MutableLiveData<InstallationAddAssignedResponse>()

    fun addAssignedRequest(sessionToken : String, propertyName : String, address:String,date:String,
                           phoneNumber:String,email:String,city:String,state:String,postalCode:String,assignedTime:String) {

        viewModelScope.launch {
            isLoading.value = true

            try {
                val apiClient = ApiClient.getApiClient()!!
                val response = ResponseHandler().handleSuccess(
                    apiClient.addAssignedRequest(sessionToken, propertyName,address,date,phoneNumber,email,city,state,postalCode,assignedTime)
                )
                isLoading.value = false
                val data= response.data?.body()
                if(data?.status == true){
                    installationSuccess.value = response.data!!.body()
                }else{
                    apiError.value = data?.message
                }

            } catch (e: Exception) {
                var exception = ResponseHandler().handleException<String>(e)
                Log.e("@@@", e.toString())
                if (e.toString().contains("401")){
                    apiError.value = "401"
                }else {
                    val exception = ResponseHandler().handleException<String>(e)
                    apiError.value = exception.message.toString()
                }
                isLoading.value = false
            }
        }
    }
}