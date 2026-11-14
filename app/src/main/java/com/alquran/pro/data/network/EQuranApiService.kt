package com.alquran.pro.data.network

import com.alquran.pro.data.model.SuratDetailResponse
import com.alquran.pro.data.model.SuratListResponse
import com.alquran.pro.data.model.TafsirResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface EQuranApiService {

    @GET("surat")
    suspend fun getSuratList(): SuratListResponse

    @GET("surat/{id}")
    suspend fun getSuratDetail(
        @Path("id") id: Int
    ): SuratDetailResponse

    @GET("tafsir/{id}")
    suspend fun getTafsir(
        @Path("id") id: Int
    ): TafsirResponse
}
