package com.poseidonapp.workorder;


import com.poseidonapp.workorder.model.pdf.PdfResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RetrofitAPI {

    @Multipart
    @POST("workOrder")
    Call<PdfResponse> uploadPdf(@Part("workOrderName") String workOrderName, @Part MultipartBody.Part imageFile);


}
