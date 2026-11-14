package com.alquran.pro.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SuratListResponse(
    @SerialName("code") val code: Int = 0,
    @SerialName("message") val message: String = "",
    @SerialName("data") val data: List<SuratSummaryDto> = emptyList()
)

@Serializable
data class SuratSummaryDto(
    @SerialName("nomor") val nomor: Int,
    @SerialName("nama") val nama: String,
    @SerialName("namaLatin") val namaLatin: String,
    @SerialName("jumlahAyat") val jumlahAyat: Int,
    @SerialName("tempatTurun") val tempatTurun: String,
    @SerialName("arti") val arti: String,
    @SerialName("deskripsi") val deskripsi: String = "",
    @SerialName("audioFull") val audioFull: Map<String, String> = emptyMap()
)

@Serializable
data class SuratDetailResponse(
    @SerialName("code") val code: Int = 0,
    @SerialName("message") val message: String = "",
    @SerialName("data") val data: SuratDetailDto? = null
)

@Serializable
data class SuratDetailDto(
    @SerialName("nomor") val nomor: Int,
    @SerialName("nama") val nama: String,
    @SerialName("namaLatin") val namaLatin: String,
    @SerialName("jumlahAyat") val jumlahAyat: Int,
    @SerialName("tempatTurun") val tempatTurun: String,
    @SerialName("arti") val arti: String,
    @SerialName("deskripsi") val deskripsi: String = "",
    @SerialName("audioFull") val audioFull: Map<String, String> = emptyMap(),
    @SerialName("ayat") val ayat: List<AyatDto> = emptyList(),
    @SerialName("suratSelanjutnya") val suratSelanjutnya: SuratNavDto? = null,
    @SerialName("suratSebelumnya") val suratSebelumnya: SuratNavDto? = null
)

@Serializable
data class SuratNavDto(
    @SerialName("nomor") val nomor: Int = 0,
    @SerialName("nama") val nama: String = "",
    @SerialName("namaLatin") val namaLatin: String = "",
    @SerialName("jumlahAyat") val jumlahAyat: Int = 0
)

@Serializable
data class AyatDto(
    @SerialName("nomorAyat") val nomorAyat: Int,
    @SerialName("teksArab") val teksArab: String,
    @SerialName("teksLatin") val teksLatin: String,
    @SerialName("teksIndonesia") val teksIndonesia: String,
    @SerialName("audio") val audio: Map<String, String> = emptyMap()
)

@Serializable
data class TafsirResponse(
    @SerialName("code") val code: Int = 0,
    @SerialName("message") val message: String = "",
    @SerialName("data") val data: TafsirDetailDto? = null
)

@Serializable
data class TafsirDetailDto(
    @SerialName("nomor") val nomor: Int,
    @SerialName("namaLatin") val namaLatin: String,
    @SerialName("tafsir") val tafsir: List<TafsirAyatDto> = emptyList()
)

@Serializable
data class TafsirAyatDto(
    @SerialName("ayat") val ayat: Int,
    @SerialName("teks") val teks: String
)
