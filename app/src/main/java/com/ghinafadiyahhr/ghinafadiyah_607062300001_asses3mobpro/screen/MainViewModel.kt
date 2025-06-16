package com.ghinafadiyahhr.ghinafadiyah_607062300001_asses3mobpro.screen

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghinafadiyahhr.ghinafadiyah_607062300001_asses3mobpro.model.Mobil
import com.ghinafadiyahhr.ghinafadiyah_607062300001_asses3mobpro.network.ApiStatus
import com.ghinafadiyahhr.ghinafadiyah_607062300001_asses3mobpro.network.MobilApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

class MainViewModel : ViewModel() {

    var data = mutableStateOf(emptyList<Mobil>())
        private set

    var status = MutableStateFlow(ApiStatus.LOADING)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    fun retrieveData(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            status.value = ApiStatus.LOADING
            try {
                data.value = MobilApi.service.getMobil(userId)
                status.value = ApiStatus.SUCCESS
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                status.value = ApiStatus.FAILED
            }
        }
    }

    fun saveData(userId: String, nama: String, deskripsi: String, bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = MobilApi.service.postMobil(
                    userId,
                    nama.toRequestBody("text/plain".toMediaTypeOrNull()),
                    deskripsi.toRequestBody("text/plain".toMediaTypeOrNull()),
                    bitmap.toMultipartBody()
                )

                if (result.status == "success") {
                    retrieveData(userId)
                } else {
                    errorMessage.value = "Gagal menyimpan data"
                }
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Terjadi kesalahan: ${e.message}"
            }
        }
    }



    fun updateData(id : String,userId: String, nama: String, deskripsi: String, bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = MobilApi.service.updateMobil(
                    id,
                    userId,
                    nama.toRequestBody("text/plain".toMediaTypeOrNull()),
                    deskripsi.toRequestBody("text/plain".toMediaTypeOrNull()),
                    bitmap.toMultipartBody()
                )

                if (result.status == "success") {
                    retrieveData(userId)
                } else {
                    errorMessage.value = "Gagal menyimpan data"
                }
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Terjadi kesalahan: ${e.message}"
            }
        }
    }

    fun deletaData(userId: String, mobilId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("MainViewModel", "=== STARTING DELETE OPERATION ===")
                Log.d("MainViewModel", "Mobil ID to delete: '$mobilId'")
                Log.d("MainViewModel", "User ID: '$userId'")

                var success = false
                var lastError = ""

                try {
                    Log.d("MainViewModel", "--- Method 1: Query Parameter ---")
                    val result = MobilApi.service.deleteMobilWithQuery(userId, mobilId)
                    Log.d("MainViewModel", "Query method response: ${result.status} - ${result.message}")

                    if (result.status == "success") {
                        success = true
                        retrieveData(userId)
                        Log.d("MainViewModel", "SUCCESS with query parameter method!")
                        return@launch
                    } else {
                        lastError = "Query method: ${result.message}"
                    }
                } catch (e: Exception) {
                    Log.d("MainViewModel", "Query method exception: ${e.message}")
                    lastError = "Query method exception: ${e.message}"
            }

                try {
                    Log.d("MainViewModel", "--- Method 2: Path Parameter ---")
                    val result = MobilApi.service.deleteMobilWithPath(userId, mobilId)
                    Log.d("MainViewModel", "Path method response: ${result.status} - ${result.message}")

                    if (result.status == "success") {
                        success = true
                        retrieveData(userId)
                        Log.d("MainViewModel", "SUCCESS with path parameter method!")
                        return@launch
                    } else {
                        lastError = "Path method: ${result.message}"
                    }
                } catch (e: Exception) {
                    Log.d("MainViewModel", "Path method exception: ${e.message}")
                    lastError = "Path method exception: ${e.message}"
                }


                val bodyFormats = listOf(
                    Triple("JSON with 'id'", """{"id":"$mobilId"}""", "application/json"),
                    Triple("JSON with 'mobil_id'", """{"mobil_id":"$mobilId"}""", "application/json"),
                    Triple("JSON with 'car_id'", """{"car_id":"$mobilId"}""", "application/json"),
                    Triple("Form data", "id=$mobilId", "application/x-www-form-urlencoded"),
                    Triple("Plain text", mobilId, "text/plain")
                )

                for ((description, bodyContent, contentType) in bodyFormats) {
                    try {
                        Log.d("MainViewModel", "--- Method 3: $description ---")
                        Log.d("MainViewModel", "Body content: '$bodyContent'")
                        Log.d("MainViewModel", "Content type: '$contentType'")

                        val requestBody = bodyContent.toRequestBody(contentType.toMediaTypeOrNull())
                        val result = MobilApi.service.deleteMobil(userId, requestBody)

                        Log.d("MainViewModel", "$description response: ${result.status} - ${result.message}")

                        if (result.status == "success") {
                            success = true
                            retrieveData(userId)
                            Log.d("MainViewModel", "SUCCESS with $description!")
                            return@launch
                        } else {
                            lastError = "$description: ${result.message}"
                        }
                    } catch (e: Exception) {
                        Log.d("MainViewModel", "$description exception: ${e.message}")
                        lastError = "$description exception: ${e.message}"
                    }
                }


                Log.d("MainViewModel", "=== ALL DELETE METHODS FAILED ===")
                Log.d("MainViewModel", "Last error: $lastError")
                throw Exception("All delete methods failed. Last error: $lastError")

            } catch (e: Exception) {
                Log.d("MainViewModel", "Delete operation failed: ${e.message}")
                errorMessage.value = "Error deleting: ${e.message}"
            }
        }
    }

    private fun Bitmap.toMultipartBody(): MultipartBody.Part {
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        val requestBody = byteArray.toRequestBody(
            "image/jpeg".toMediaTypeOrNull(), 0, byteArray.size
        )
        return MultipartBody.Part.createFormData("image", "image.jpg", requestBody)
    }

    fun clearMessage() {
        errorMessage.value = null
    }
}
