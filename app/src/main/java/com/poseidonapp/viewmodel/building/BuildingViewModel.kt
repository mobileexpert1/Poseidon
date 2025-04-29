package com.poseidonapp.viewmodel.building

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.poseidonapp.model.addAddress.AddAddressResponse
import com.poseidonapp.model.addbuilding.AddBuildingResponse
import com.poseidonapp.model.building.BuildingResponse
import com.poseidonapp.retrofit.ApiClient
import com.poseidonapp.retrofit.ResponseHandler
import com.poseidonapp.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class BuildingViewModel : BaseViewModel() {
    //AddBuilding

    var addBuildingSuccess= MutableLiveData<AddBuildingResponse>()
    fun addBuildingRequest(addressId:String, name:String){
        viewModelScope.launch {
            isLoading.value=true
            try {
                val apiClient= ApiClient.getApiClient()
                val response= ResponseHandler().handleSuccess(
                    apiClient!!.addBuildingRequest(addressId,name)
                )
                isLoading.value=false
                val data= response.data

                if(data?.status == true){
                    addBuildingSuccess.value = data.let { it }
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


    //Building
    var buildingSuccess= MutableLiveData<BuildingResponse>()
    fun buildingRequest(sessionToken:String,addressId:String){
        viewModelScope.launch {
            isLoading.value=true
            try {
                val apiClient= ApiClient.getApiClient()
                val response= ResponseHandler().handleSuccess(apiClient!!.buildingRequest(sessionToken,addressId))
                isLoading.value=false
                val data= response.data

                if(data?.status == true){

                    buildingSuccess.value = data.let { it }
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