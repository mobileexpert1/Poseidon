package com.poseidonapp.viewmodel.addSystemfromReport

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.poseidonapp.model.addSystemreport.AddSystemFromReportResponse
import com.poseidonapp.retrofit.ApiClient
import com.poseidonapp.retrofit.ResponseHandler
import com.poseidonapp.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class AddSystemFromReportViewModel: BaseViewModel() {


    var addSystemFromReportSuccess= MutableLiveData<AddSystemFromReportResponse>()

    fun addSystemFromReportRequest(requestId: String,systemId: String,systemDescription: String,assignedQuestion: String,){
        viewModelScope.launch {
            isLoading.value=true
            try {
                val apiClient= ApiClient.getApiClient()
                val response= ResponseHandler().handleSuccess(
                    apiClient!!.addSystemFromReportRequest(requestId,systemId,systemDescription,assignedQuestion)
                )
                isLoading.value=false
                val data= response.data

                if(data?.status == true){
                    addSystemFromReportSuccess.value = data.let { it }
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