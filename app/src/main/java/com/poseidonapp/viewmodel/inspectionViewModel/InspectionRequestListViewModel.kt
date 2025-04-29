package com.poseidonapp.viewmodel.inspectionViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.poseidonapp.model.inspection.InspectionResponseData
import com.poseidonapp.retrofit.ApiClient
import com.poseidonapp.retrofit.ResponseHandler
import com.poseidonapp.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class InspectionRequestListViewModel:BaseViewModel() {

    var inspectionRequestListSuccess= MutableLiveData<InspectionResponseData>()

    fun inspectionRequestList(sessionToken:String,filterByDate:String){
        viewModelScope.launch {
            isLoading.value=true
            try {
                val apiClient= ApiClient.getApiClient()
                val response= ResponseHandler().handleSuccess(
                    apiClient!!.addRequestInspection(sessionToken,filterByDate)
                )
                isLoading.value=false
                val data= response.data

                if(data?.status == true){
                    inspectionRequestListSuccess.value = data.let { it }
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