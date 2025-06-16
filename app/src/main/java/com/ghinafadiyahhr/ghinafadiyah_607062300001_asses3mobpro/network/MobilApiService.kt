package com.ghinafadiyahhr.ghinafadiyah_607062300001_asses3mobpro.network

import com.ghinafadiyahhr.ghinafadiyah_607062300001_asses3mobpro.model.Mobil
import com.ghinafadiyahhr.ghinafadiyah_607062300001_asses3mobpro.model.OpStatus
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL = "https://apimobpromobil-production.up.railway.app/api/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface MobilApiService {
    @GET("mobil")
    suspend fun getMobil(
        @Header("Authorization"
        ) userId: String): List<Mobil>

    @Multipart
    @POST("mobil")
    suspend fun postMobil(
        @Header("Authorization") userId: String,
        @Part("nama") nama: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part image: MultipartBody.Part
    ): OpStatus

    @Multipart
    @POST("mobil/{id}")
    suspend fun updateMobil(
        @Path("id") id :String,
        @Header("Authorization") userId: String,
        @Part("nama") nama: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part image: MultipartBody.Part
    ): OpStatus

    @HTTP(method = "DELETE", path = "mobil", hasBody = true)
    suspend fun deleteMobil(
        @Header("Authorization") userId: String,
        @Body body: RequestBody
    ): OpStatus

    @DELETE("mobil")
    suspend fun deleteMobilWithQuery(
        @Header("Authorization") userId: String,
        @Query("id") mobilId: String
    ): OpStatus


    @DELETE("mobil/{id}")
    suspend fun deleteMobilWithPath(
        @Header("Authorization") userId: String,
        @Path("id") mobilId: String
    ): OpStatus
}

object MobilApi{
    val service: MobilApiService by lazy {
        retrofit.create(MobilApiService::class.java)
    }

    fun getMobilUrl(image: String): String {
        return "https://apimobpromobil-production.up.railway.app/storage/$image"
    }
}

enum class ApiStatus { LOADING, SUCCESS, FAILED }