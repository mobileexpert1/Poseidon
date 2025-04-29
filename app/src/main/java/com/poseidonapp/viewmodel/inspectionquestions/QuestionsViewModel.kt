package com.poseidonapp.viewmodel.inspectionquestions

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope

import com.poseidonapp.model.questions.QuestionJson
import com.poseidonapp.model.questions.QuestionsResponse
import com.poseidonapp.retrofit.ApiClient
import com.poseidonapp.retrofit.ResponseHandler
import com.poseidonapp.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class QuestionsViewModel: BaseViewModel() {
    //    var questionsListSuccess= MutableLiveData<QuestionsListResponse>()
    var questionsListSuccess= MutableLiveData<QuestionsResponse>()

    fun questionsListRequest(requestId: String){
        viewModelScope.launch {
            isLoading.value=true
            try {
                val apiClient= ApiClient.getApiClientQues()
                val response= ResponseHandler().handleSuccess(
                    apiClient!!.questionListRequest(requestId)
                )
                isLoading.value=false
                val data= response.data

                if(data?.status == true){
                    questionsListSuccess.value = data.let { it }
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