package com.alquran.pro.data.network

import com.alquran.pro.data.model.JadwalResponse
import com.alquran.pro.data.model.KotaListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MyQuranApiService {

    @GET("sholat/kabkota/cari/{keyword}")
    suspend fun searchKota(
        @Path("keyword") keyword: String
    ): KotaListResponse

    @GET("sholat/kabkota/semua")
    suspend fun getAllKota(): KotaListResponse

    @GET("sholat/jadwal/{id}/today")
    suspend fun getJadwalToday(
        @Path("id") id: String,
        @Query("tz") timezone: String = "Asia/Jakarta"
    ): JadwalResponse
}
