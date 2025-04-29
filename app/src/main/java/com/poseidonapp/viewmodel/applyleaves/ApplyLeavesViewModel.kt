package com.poseidonapp.viewmodel.applyleaves

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.poseidonapp.model.applyleave.ApplyLeavesResponse
import com.poseidonapp.model.clockInlistscreen.ClockInListScreenResponse
import com.poseidonapp.retrofit.ApiClient
import com.poseidonapp.retrofit.ResponseHandler
import com.poseidonapp.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import retrofit2.http.Field
import java.time.LocalDate

class ApplyLeavesViewModel : BaseViewModel(){


    var applyLeaveSuccess = MutableLiveData<ApplyLeavesResponse>()

    fun applyLeaveRequest(sessionToken : String, leaveDates : String, reason:String, type:String) {

        viewModelScope.launch {
            isLoading.value = true

            try {
                val apiClient = ApiClient.getApiClient()!!
                val response = ResponseHandler().handleSuccess(
                    apiClient.applyLeaveRequest(
                        sessionToken, leaveDates,reason,type
                    )
                )
                isLoading.value = false

                val data= response.data?.body()

                if(data?.status == true){
                    applyLeaveSuccess.value = response.data.body()
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