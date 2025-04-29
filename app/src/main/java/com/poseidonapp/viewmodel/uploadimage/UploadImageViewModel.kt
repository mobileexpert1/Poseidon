package com.poseidonapp.viewmodel.uploadimage

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.poseidonapp.model.uploadimage.UploadResponse
import com.poseidonapp.retrofit.ApiClient
import com.poseidonapp.retrofit.ResponseHandler
import com.poseidonapp.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class UploadImageViewModel : BaseViewModel(){

    var uploadImageSuccess = MutableLiveData<UploadResponse>()

    fun uploadImageRequest(image: String) {

        viewModelScope.launch {
            isLoading.value = true

            try {
                val apiClient = ApiClient.getApiClient()!!
                val response = ResponseHandler().handleSuccess(
                    apiClient.uploadImageRequest(image)
                )
                isLoading.value = false

                val data= response.data?.body()

                if(data?.status == true){
                    uploadImageSuccess.value = response.data.body()
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