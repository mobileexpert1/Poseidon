package com.poseidonapp.viewmodel.addchat

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.poseidonapp.model.addchat.AddChatResponse
import com.poseidonapp.retrofit.ApiClient
import com.poseidonapp.retrofit.ResponseHandler
import com.poseidonapp.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class AddChatViewModel:BaseViewModel() {

    var addChatSuccess = MutableLiveData<AddChatResponse>()

    fun addChatRequest(sessionToken : String, userNoteId : String, text:String) {

        viewModelScope.launch {
            isLoading.value = true

            try {
                val apiClient = ApiClient.getApiClient()!!
                val response = ResponseHandler().handleSuccess(
                    apiClient.addMessageRequest(sessionToken, userNoteId,text)
                )
                isLoading.value = false
                val data= response.data?.body()
                if(data?.status == true){
                    addChatSuccess.value = response.data?.body()
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