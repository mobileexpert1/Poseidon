package com.poseidonapp.viewmodel.respondmeetingrequest

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.poseidonapp.model.respondmeetingrequest.RespondMeetingRequestResponse
import com.poseidonapp.retrofit.ApiClient
import com.poseidonapp.retrofit.ResponseHandler
import com.poseidonapp.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class RespondMeetingRequestViewModel : BaseViewModel(){

    var respondMeetingRequestSuccess = MutableLiveData<RespondMeetingRequestResponse>()

    fun respondMeetingRequest(sessionToken : String, meetingId : String, status:String) {

        viewModelScope.launch {
            isLoading.value = true

            try {
                val apiClient = ApiClient.getApiClient()!!
                val response = ResponseHandler().handleSuccess(
                    apiClient.respondMeetingRequest(
                        sessionToken, meetingId,status
                    )
                )
                isLoading.value = false

                val data= response.data?.body()

                if(data?.status == true){
                    respondMeetingRequestSuccess.value = response.data.body()
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


}