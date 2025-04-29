package com.poseidonapp.workorder;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public RetrofitAPI getApiClient(){
        Gson gson=new GsonBuilder().setLenient().create();

        OkHttpClient.Builder httpClient=new OkHttpClient.Builder().connectTimeout(3, TimeUnit.MINUTES)
                .writeTimeout(3,TimeUnit.MINUTES)
                .readTimeout(3,TimeUnit.MINUTES);

/*        HttpLoggingInterceptor interceptor=new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(interceptor);

        httpClient.addInterceptor(new BasicInterceptor(context,type,"aGJpdWtzaGRyc2VoZGZ5ajZya","dGd5aDZ1NzM1NjQ3NWVnZGVycw=="));*/



        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://poseidon-fire.com/api/inspector/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();

        return retrofit.create(RetrofitAPI.class);
    }

}
