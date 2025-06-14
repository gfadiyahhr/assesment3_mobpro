package com.ghinafadiyahhr.ghinafadiyah_607062300001_asses3mobpro.network

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://684d565e65ed08713915cdc0.mockapi.io/mobilf1/api/v1/"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface MobilApiService {
    @GET("merekmobil")
    suspend fun getMobil(): String
}

object MobilApi{
    val service: MobilApiService by lazy {
        retrofit.create(MobilApiService::class.java)
    }
}