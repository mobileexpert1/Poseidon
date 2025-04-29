package com.poseidonapp.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.poseidonapp.model.addchat.AddChatResponse
import com.poseidonapp.model.inspectionDetail.IspectionDetailListResponse
import com.poseidonapp.retrofit.ApiClient
import com.poseidonapp.retrofit.ResponseHandler
import com.poseidonapp.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import retrofit2.http.Field

class AddInspectionDetailViewModel: BaseViewModel() {

    var ispectionDetails = MutableLiveData<IspectionDetailListResponse>()


    fun addInspectionDetail(sessionToken : String,fullName : String,emailAddress : String,phoneNumber : String,adddressLine1 : String,
                            billTo : String,city : String,state : String,postalCode : String,assignDate : String,assignTime : String,
                            inspectionType : String, type:String,buildingName:String
    ) {

        viewModelScope.launch {
            isLoading.value = true

            try {
                val apiClient = ApiClient.getApiClient()!!
                val response = ResponseHandler().handleSuccess(
                    apiClient.addInspectionDetailList(sessionToken,fullName,emailAddress,phoneNumber,adddressLine1,billTo,city,state,postalCode,assignDate,
                        assignTime,inspectionType,type,buildingName)
                )
                isLoading.value = false
                val data= response.data?.body()
                if(data?.status == true){
                    ispectionDetails.value = response.data?.body()
                }else{
                    apiError.value = data?.message
                }

            } catch (e: Exception) {
                var exception = ResponseHandler().handleException<String>(e)
                Log.e("@@@", e.toString())
                if (e.toString().contains("401")){
                    apiError.value = "401"
                }else {
                    val exception = ResponseHandler().handleException<String>(e)
                    apiError.value = exception.message.toString()
                }
                isLoading.value = false
            }
        }
    }
}