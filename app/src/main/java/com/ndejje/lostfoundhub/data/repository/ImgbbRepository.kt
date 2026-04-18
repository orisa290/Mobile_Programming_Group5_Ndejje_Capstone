package com.ndejje.lostfoundhub.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream

class ImgbbRepository(private val context: Context) {

    private val apiKey = "73fc760c099c679ebe439e10bf8b5f75"

    private val api: ImgbbApi by lazy {
        val client = OkHttpClient.Builder()
            .build()

        Retrofit.Builder()
            .baseUrl("https://api.imgbb.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ImgbbApi::class.java)
    }

    suspend fun uploadImage(imageUri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d("Imgbb", "========== UPLOAD START ==========")
            Log.d("Imgbb", "API Key: ${apiKey.take(10)}...")
            Log.d("Imgbb", "Image URI: $imageUri")

            val file = uriToFile(imageUri)
            Log.d("Imgbb", "File size: ${file.length()} bytes")

            val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())

            val keyPart = MultipartBody.Part.createFormData("key", apiKey)
            val imagePart = MultipartBody.Part.createFormData("image", file.name, requestBody)

            Log.d("Imgbb", "Sending request to ImgBB...")

            // ✅ Direct call - throws exception on failure
            val response = api.uploadImage(keyPart, imagePart)

            Log.d("Imgbb", "✅ SUCCESS! URL: ${response.data.url}")
            Result.success(response.data.url)

        } catch (e: HttpException) {
            // Handle HTTP errors (4xx, 5xx)
            Log.e("Imgbb", "HTTP Error: ${e.code()} - ${e.message()}")
            Result.failure(Exception("Upload failed: ${e.code()}"))
        } catch (e: Exception) {
            // Handle other errors
            Log.e("Imgbb", "Exception: ${e.message}", e)
            Result.failure(e)
        }
    }

    private fun uriToFile(uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw Exception("Cannot open input stream")
        val tempFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(tempFile)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        return tempFile
    }
}
