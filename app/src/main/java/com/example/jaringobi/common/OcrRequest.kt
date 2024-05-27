package com.example.jaringobi.common

import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST

interface OcrRequest {
    @POST
    fun getOcr(
        @Header("X-OCR-SECRET") token: String,
    ): Call<Unit>
}
