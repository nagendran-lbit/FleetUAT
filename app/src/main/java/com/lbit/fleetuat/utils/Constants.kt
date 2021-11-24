package com.lbit.fleetuat.utils



import com.lbit.fleetuat.retrofit_service.RetrofitService
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object Constants {

    val URL = "https://devpayroll.lbit.co.in/"

    val PREOWNED_BASE_URL = "http://usedcar.letzbank.com/"
    val PHP_URL = "http://13.127.220.54:1213/"
    val CAMPAIGN_URL = "http://lbit.letzbank.com/"
    val OTHER = "Other"
    val fleet_url = "https://uatfleet.lbit.co.in/"
//    val payroll_url = "https://devpayroll.lbit.co.in/"
//    val payroll_url = "https://payrollikf.lbit.co.in/"

    val okHttpClient = OkHttpClient.Builder()
        .readTimeout(80000, TimeUnit.SECONDS)
        .connectTimeout(80000, TimeUnit.SECONDS)
        .build()

    val Fleet_URL = Retrofit.Builder()
        .baseUrl(fleet_url)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build().create<RetrofitService>(RetrofitService::class.java)

    val CAMPAIGN_Url = Retrofit.Builder()
        .baseUrl(CAMPAIGN_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build().create<RetrofitService>(RetrofitService::class.java)
}