package com.poseidonapp.viewmodel.addRequest

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.poseidonapp.model.addRequest.AddRequestResponse
import com.poseidonapp.retrofit.ApiClient
import com.poseidonapp.retrofit.ResponseHandler
import com.poseidonapp.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class AddRequestViewModel: BaseViewModel() {
    var addRequestSuccess= MutableLiveData<AddRequestResponse>()
    fun addRequestRequest(organizationId:String, addressId:String,inspectionType:String,assignTime:String,sessionToken:String,assignDate:String,buildingId:String,billTo:String,notes:String){
        viewModelScope.launch {
            isLoading.value=true
            try {
                val apiClient= ApiClient.getApiClient()
                val response= ResponseHandler().handleSuccess(
                    apiClient!!.addRequestRequest(organizationId,addressId,inspectionType,assignTime,sessionToken,assignDate,buildingId,billTo,notes)
                )
                isLoading.value=false
                val data= response.data

                if(data?.status == true){
                    addRequestSuccess.value = data.let { it }
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