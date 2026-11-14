package com.alquran.pro.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KotaListResponse(
    @SerialName("status") val status: Boolean = false,
    @SerialName("message") val message: String = "",
    @SerialName("data") val data: List<KotaDto> = emptyList()
)

@Serializable
data class KotaDto(
    @SerialName("id") val id: String,
    @SerialName("lokasi") val lokasi: String
)

@Serializable
data class JadwalResponse(
    @SerialName("status") val status: Boolean = false,
    @SerialName("message") val message: String = "",
    @SerialName("data") val data: JadwalContainerDto? = null
)

@Serializable
data class JadwalContainerDto(
    @SerialName("id") val id: String = "",
    @SerialName("kabko") val kabko: String = "",
    @SerialName("prov") val prov: String = "",
    @SerialName("jadwal") val jadwal: Map<String, JadwalHariIniDto> = emptyMap()
)

@Serializable
data class JadwalHariIniDto(
    @SerialName("tanggal") val tanggal: String = "",
    @SerialName("imsak") val imsak: String = "",
    @SerialName("subuh") val subuh: String = "",
    @SerialName("terbit") val terbit: String = "",
    @SerialName("dhuha") val dhuha: String = "",
    @SerialName("dzuhur") val dzuhur: String = "",
    @SerialName("ashar") val ashar: String = "",
    @SerialName("maghrib") val maghrib: String = "",
    @SerialName("isya") val isya: String = ""
)
