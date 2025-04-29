package com.poseidonapp.viewmodel.createproject

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.poseidonapp.model.addexpense.AddExpenseResponse
import com.poseidonapp.model.createproject.CreateProjectResponse
import com.poseidonapp.retrofit.ApiClient
import com.poseidonapp.retrofit.ResponseHandler
import com.poseidonapp.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import retrofit2.http.Field

class CreateProjectViewModel: BaseViewModel(){

    var createProjectSuccess = MutableLiveData<CreateProjectResponse>()

    fun createProjectRequest(sessionToken : String, name : String, contractorName:String, lat:String, long:String, address:String, city:String, state:String, postalCode:String) {

        viewModelScope.launch {
            isLoading.value = true

            try {
                val apiClient = ApiClient.getApiClient()!!
                val response = ResponseHandler().handleSuccess(
                    apiClient.createProjectRequest(
                        sessionToken, name,contractorName,lat,long,address,city,state,postalCode
                    )
                )
                isLoading.value = false

                val data= response.data?.body()

                if(data?.status == true){
                    createProjectSuccess.value = response.data.body()
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