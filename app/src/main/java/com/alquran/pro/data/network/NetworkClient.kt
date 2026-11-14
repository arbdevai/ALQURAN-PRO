package com.alquran.pro.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object NetworkClient {

    private const val EQURAN_BASE_URL = "https://equran.id/api/v2/"
    private const val MYQURAN_BASE_URL = "https://api.myquran.com/v3/"

    val json: Json by lazy {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
            coerceInputValues = true
        }
    }

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.NONE
        }

        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    private val mediaType = "application/json".toMediaType()

    val equranApi: EQuranApiService by lazy {
        Retrofit.Builder()
            .baseUrl(EQURAN_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(mediaType))
            .build()
            .create(EQuranApiService::class.java)
    }

    val myquranApi: MyQuranApiService by lazy {
        Retrofit.Builder()
            .baseUrl(MYQURAN_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(mediaType))
            .build()
            .create(MyQuranApiService::class.java)
    }
}
