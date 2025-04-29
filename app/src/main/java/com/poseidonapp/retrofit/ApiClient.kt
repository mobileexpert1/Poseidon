package com.poseidonapp.retrofit

import android.annotation.SuppressLint

import com.google.gson.GsonBuilder
import com.poseidonapp.utils.Const
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory

object ApiClient {

//   val sharedPref = SharedPref(MainApplication.instance?.let { it }!!)

    fun getApiClient(): ApiInterface? {

        val gson=GsonBuilder().setLenient().create()
        val httpClient=OkHttpClient.Builder().connectTimeout(4,TimeUnit.MINUTES)
            .writeTimeout(4,TimeUnit.MINUTES)
            .readTimeout(4,TimeUnit.MINUTES)

        httpClient.addInterceptor { chain ->
            var newRequest : Request?
          /*  if (sharedPref.getToken(Const.TOKEN).length > 1) {
                newRequest = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .addHeader("Authorization", "Bearer " + sharedPref.getToken(Const.TOKEN))
                    .build()
            } else { */
                newRequest = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json; charset=utf-8")
//                    .addHeader("Authorization", "Bearer " + sharedPref.getToken(Const.TOKEN))
                    .build()
//            }

            chain.proceed(newRequest)

        }.build()

        val interceptor= HttpLoggingInterceptor()
        interceptor.level=HttpLoggingInterceptor.Level.BODY

        httpClient.addInterceptor(interceptor)

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(Const.BASE_URL)
            .client(httpClient.build())
            .build()

        return retrofit.create(ApiInterface::class.java)

    }

    fun getApiClientQues(): ApiInterface? {

        val gson=GsonBuilder().setLenient().create()
        val httpClient=OkHttpClient.Builder()
            .connectTimeout(3,TimeUnit.MINUTES)
            .writeTimeout(3,TimeUnit.MINUTES)
            .readTimeout(3,TimeUnit.MINUTES)

        httpClient.addInterceptor { chain ->
            var newRequest : Request?
          /*  if (sharedPref.getToken(Const.TOKEN).length > 1) {
                newRequest = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .addHeader("Authorization", "Bearer " + sharedPref.getToken(Const.TOKEN))
                    .build()
            } else { */
                newRequest = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json; charset=utf-8")
//                    .addHeader("Authorization", "Bearer " + sharedPref.getToken(Const.TOKEN))
                    .build()
//            }

            chain.proceed(newRequest)

        }.build()

        val interceptor= HttpLoggingInterceptor()
        interceptor.level=HttpLoggingInterceptor.Level.BODY

        httpClient.addInterceptor(interceptor)

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(Const.BASE_URL_QUES)
            .client(httpClient.build())
            .build()

        return retrofit.create(ApiInterface::class.java)

    }

    fun getApiClientWithHeader(token:String): ApiInterface? {
        val gson=GsonBuilder().setLenient().create()

        val httpClient=OkHttpClient.Builder().connectTimeout(3,TimeUnit.MINUTES)
            .writeTimeout(3,TimeUnit.MINUTES)
            .readTimeout(3,TimeUnit.MINUTES)

          httpClient.addInterceptor { chain ->
              val newRequest = chain.request().newBuilder()
                  .addHeader("Content-Type", "application/json; charset=utf-8")
//                  .addHeader("Authorization", token)
//                  .addHeader("clientsite", "MOA")
                  .build()
              chain.proceed(newRequest)

          }.build()

        val interceptor= HttpLoggingInterceptor()
        interceptor.level=HttpLoggingInterceptor.Level.BODY

        httpClient.addInterceptor(interceptor)

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(Const.BASE_URL)
            .client(httpClient.build())
            .build()

        return retrofit.create(ApiInterface::class.java)

    }





}