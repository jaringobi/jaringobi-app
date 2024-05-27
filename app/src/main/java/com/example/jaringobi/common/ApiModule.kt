package com.example.jaringobi.common

import com.example.jaringobi.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiModule {
    private fun getRetrofit(): Retrofit =
        Retrofit.Builder().baseUrl(BuildConfig.OCR_API_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()

    fun getOcr(): OcrRequest {
        return getRetrofit().create(OcrRequest::class.java)
    }
}
