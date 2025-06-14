package com.ghinafadiyahhr.ghinafadiyah_607062300001_asses3mobpro.network

import com.ghinafadiyahhr.ghinafadiyah_607062300001_asses3mobpro.model.Mobil
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://684d565e65ed08713915cdc0.mockapi.io/mobilf1/api/v1/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface MobilApiService {
    @GET("merekmobil")
    suspend fun getMobil(): List<Mobil>
}

object MobilApi{
    val service: MobilApiService by lazy {
        retrofit.create(MobilApiService::class.java)
    }

    fun getMobilUrl(image: String): String {
        return "$BASE_URL$image.jpg"
    }
}

enum class ApiStatus { LOADING, SUCCESS }