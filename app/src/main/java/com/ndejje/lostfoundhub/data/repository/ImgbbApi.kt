package com.ndejje.lostfoundhub.data.repository

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ImgbbApi {
    @Multipart
    @POST("1/upload")
    suspend fun uploadImage(
        @Part apiKey: MultipartBody.Part,
        @Part image: MultipartBody.Part
    ): ImgbbUploadResponse
}

data class ImgbbUploadResponse(
    val data: ImgbbImageData,
    val success: Boolean,
    val status: Int
)

data class ImgbbImageData(
    val url: String,
    val display_url: String,
    val title: String?
)
