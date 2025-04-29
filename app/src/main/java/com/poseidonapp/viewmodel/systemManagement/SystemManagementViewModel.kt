package com.poseidonapp.viewmodel.systemMangement

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.poseidonapp.model.addAddress.AddAddressResponse
import com.poseidonapp.model.addOrganization.AddOrganizationResponse
import com.poseidonapp.model.addsystemManagement.AddSystemResponse
import com.poseidonapp.model.systemManagement.SystemManagementResponse
import com.poseidonapp.retrofit.ApiClient
import com.poseidonapp.retrofit.ResponseHandler
import com.poseidonapp.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class SystemManagementViewModel: BaseViewModel()  {

    //AddSystemManagement
    var addSystemSuccess= MutableLiveData<AddSystemResponse>()
    fun addSystemRequest(systemDescription:String, assignedQuestion:String,addressId:String,systemId:String){
        viewModelScope.launch {
            isLoading.value=true
            try {
                val apiClient= ApiClient.getApiClient()
                val response= ResponseHandler().handleSuccess(
                    apiClient!!.addSystemRequest(systemDescription,assignedQuestion,addressId,systemId)
                )
                isLoading.value=false
                val data= response.data

                if(data?.status == true){
                    addSystemSuccess.value = data.let { it }
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


    //Systemmanagement

    var systemSuccess= MutableLiveData<SystemManagementResponse>()

    fun systemRequest(sessionToken:String,buildingId:String){
        viewModelScope.launch {
            isLoading.value=true
            try {
                val apiClient= ApiClient.getApiClient()
                val response= ResponseHandler().handleSuccess(
                    apiClient!!.systemManagementRequest(sessionToken,buildingId)
                )
                isLoading.value=false
                val data= response.data

                if(data?.status == true){
                    systemSuccess.value = data.let { it }
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