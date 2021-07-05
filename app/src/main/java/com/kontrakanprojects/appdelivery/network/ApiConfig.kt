package com.kontrakanprojects.appdelivery.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        // hosting main
        private const val URL = "https://delivery.rproject-dev.com"

        // secondary hosting
//        private const val URL = "https://keqing123.000webhostapp.com"

//        private const val URL = "https://aruvin.000webhostapp.com"

        // second hosting
        const val IMG_URL = "$URL/public"

        private const val ENDPOINT = "$URL/api/"

        private fun client(): OkHttpClient {
            val logging = HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY)
            return OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
        }

        fun getApiService(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client())
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}