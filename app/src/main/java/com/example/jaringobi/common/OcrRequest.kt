package com.example.jaringobi.common

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.UUID

interface OcrRequest {
    @POST("document/receipt")
    fun getOcr(
        @Header("X-OCR-SECRET") token: String,
        @Body body: RequestBody,
    ): Call<OcrResponse>
}

data class RequestBody(
    val version: String = "V2",
    val requestId: String = UUID.randomUUID().toString(),
    val images: List<ImageData>,
    val timestamp: Long = System.currentTimeMillis(),
)

data class ImageData(
    val format: String,
    val name: String = "receipt",
    val data: String,
)
