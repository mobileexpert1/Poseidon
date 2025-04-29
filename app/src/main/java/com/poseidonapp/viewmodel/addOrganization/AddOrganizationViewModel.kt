package com.poseidonapp.viewmodel.addOrganization

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.poseidonapp.model.addOrganization.AddOrganizationResponse
import com.poseidonapp.retrofit.ApiClient
import com.poseidonapp.retrofit.ResponseHandler
import com.poseidonapp.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class AddOrganizationViewModel: BaseViewModel() {

    var addOrganizationSuccess= MutableLiveData<AddOrganizationResponse>()


    fun addOrganizationRequest(phoneNumber:String, organizationName:String,emailAddress:String,fullName:String){
        viewModelScope.launch {
            isLoading.value=true
            try {
                val apiClient= ApiClient.getApiClient()
                val response= ResponseHandler().handleSuccess(
                    apiClient!!.addOrganizationRequest(phoneNumber,organizationName,emailAddress,fullName)
                )
                isLoading.value=false
                val data= response.data

                if(data?.status == true){
                    addOrganizationSuccess.value = data.let { it }
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