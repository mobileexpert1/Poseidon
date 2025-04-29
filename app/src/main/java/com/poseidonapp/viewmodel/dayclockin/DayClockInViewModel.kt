package com.poseidonapp.viewmodel.dayclockin

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.poseidonapp.model.dayclockin.DayClockInResponse
import com.poseidonapp.retrofit.ApiClient
import com.poseidonapp.retrofit.ResponseHandler
import com.poseidonapp.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class DayClockInViewModel : BaseViewModel(){

    var dayClockInSuccess = MutableLiveData<DayClockInResponse>()

    fun dayClockInRequest(sessionToken : String, lat : String, long:String, image:String, injured:String) {

        viewModelScope.launch {
            isLoading.value = true

            try {
                val apiClient = ApiClient.getApiClient()!!
                val response = ResponseHandler().handleSuccess(
                    apiClient.dayClockInRequest(
                        sessionToken, lat,long,image,injured
                    )
                )
                isLoading.value = false

                val data= response.data?.body()

                if(data?.status == true){
                    dayClockInSuccess.value = response.data.body()
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