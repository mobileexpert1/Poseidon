package com.poseidonapp.viewmodel.address

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.poseidonapp.model.addAddress.AddAddressResponse
import com.poseidonapp.model.address.AddressResponse
import com.poseidonapp.retrofit.ApiClient
import com.poseidonapp.retrofit.ResponseHandler
import com.poseidonapp.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class AddressViewModel: BaseViewModel() {
    //AddAdress
    var addAddressSuccess= MutableLiveData<AddAddressResponse>()
    fun addAddressRequest(addressLine:String, organizationsId:String,city:String,postalCode:String,state:String){
        viewModelScope.launch {
            isLoading.value=true
            try {
                val apiClient= ApiClient.getApiClient()
                val response= ResponseHandler().handleSuccess(
                    apiClient!!.addAddressRequest(addressLine,organizationsId,city,postalCode,state)
                )
                isLoading.value=false
                val data= response.data

                if(data?.status == true){
                    addAddressSuccess.value = data.let { it }
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



    // Address
    var addressSuccess= MutableLiveData<AddressResponse>()

    fun addressRequest(sessionToken:String,organizationId:String){
        viewModelScope.launch {
            isLoading.value=true
            try {
                val apiClient= ApiClient.getApiClient()
                val response= ResponseHandler().handleSuccess(
                    apiClient!!.addressRequest(sessionToken,organizationId)
                )
                isLoading.value=false
                val data= response.data

                if(data?.status == true){
                    addressSuccess.value = data.let { it }
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