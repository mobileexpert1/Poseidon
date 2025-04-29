package com.poseidonapp.viewmodel.weektimesheet

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.poseidonapp.model.addexpense.AddExpenseResponse
import com.poseidonapp.model.weektimesheet.WeekTimeSheetResponse
import com.poseidonapp.model.weektimesheetBody.WeekTimeSheetBody
import com.poseidonapp.retrofit.ApiClient
import com.poseidonapp.retrofit.ResponseHandler
import com.poseidonapp.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class WeekTimeSheetViewModel: BaseViewModel(){

    var weekTimeSheetSuccess = MutableLiveData<WeekTimeSheetResponse>()

    fun weekTimeSheetRequest(sessionToken : String, weekData: String) {

        viewModelScope.launch {
            isLoading.value = true

            try {
                val apiClient = ApiClient.getApiClient()!!
                val response = ResponseHandler().handleSuccess(
                    apiClient.weekTimeSheetRequest(
                        sessionToken, weekData
                    )
                )
                isLoading.value = false

                val data= response.data?.body()

                if(data?.status == true){
                    weekTimeSheetSuccess.value = response.data.body()
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