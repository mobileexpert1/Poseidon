package com.poseidonapp.viewmodel.inspectorReportClone

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.poseidonapp.model.inspectorresportclone.InpectorReportClonResponse
import com.poseidonapp.retrofit.ApiClient
import com.poseidonapp.retrofit.ResponseHandler
import com.poseidonapp.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import retrofit2.http.Field

class InspectorReportCloneViewModel : BaseViewModel(){

    var inspectorReportCloneSuccess = MutableLiveData<InpectorReportClonResponse>()

    // fun inspectorReportCloneRequest(sessionToken : String,requestId: String,customerSign: String,
//                                 inspectorSign: String,timeTake: String,questionJson: String,graphBase64: String,
//                                 flowTestTableValues: String, additionalFields: HashMap<String, String> ) {
    fun inspectorReportCloneRequest(   sessionToken: String,
                                       requestId: String,
                                       customerSign: String,
                                       inspectorSign: String,
                                       questionJson: String,
                                       flowTestTableValues: String,
                                       graphBase64: String,
                                       timeTake: String,
                                       winterization: String,
                                       fireExtinguisher: String,
                                       systemLocationText: String,
                                       chart1: String,
                                       chart2: String,
                                       additionalFields: HashMap<String, String>
    ) {

        viewModelScope.launch {
            isLoading.value = true
            try {

                val apiClient = ApiClient.getApiClientQues()!!
                val response = ResponseHandler().handleSuccess(
                    apiClient.inspectorReportCloneRequest(
                        sessionToken = sessionToken, requestId = requestId, customerSign = customerSign, inspectorSign = inspectorSign, questionJson = questionJson,
                        flowTestTableValues = flowTestTableValues,
                        graphBase64 = graphBase64, timeTake = timeTake, winterization = winterization,
                        fireExtinguisher = fireExtinguisher, systemLocationText = systemLocationText,
                        chart1 = chart1, chart2 =chart2,additionalFields
                    )
                )/* val response = ResponseHandler().handleSuccess(
                    apiClient.inspectorReportCloneRequest(
                        sessionToken,requestId,customerSign,inspectorSign,timeTake,questionJson,graphBase64,flowTestTableValues,additionalFields
                    )
                )*/
                isLoading.value = false

                val data= response.data?.body()

                if(data?.status == true){
                    inspectorReportCloneSuccess.value = response.data.body()
                }else{
                    apiError.value = data?.message
                }

            } catch (e: Exception) {
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



    /*
    fun inspectorReportCloneRequest(sessionToken : String,requestId: String,customerSign: String,
                                     inspectorSign: String,timeTake: String,questionJson: String,graphBase64: String,
                                     flowTestTableValues: String, additionalFields: HashMap<String, String> ) {

            viewModelScope.launch {
                isLoading.value = true
                try {
                    val apiClient = ApiClient.getApiClient()!!
                    val response = ResponseHandler().handleSuccess(
                        apiClient.inspectorReportCloneRequest(
                            sessionToken,requestId,customerSign,inspectorSign,timeTake,questionJson,graphBase64,flowTestTableValues,additionalFields
                        )
                    )
                    isLoading.value = false

                    val data= response.data?.body()

                    if(data?.status == true){
                        inspectorReportCloneSuccess.value = response.data.body()
                    }else{
                        apiError.value = data?.message
                    }

                } catch (e: Exception) {
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
    */


}