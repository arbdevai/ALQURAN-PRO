package com.alquran.pro.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BookmarkItem(
    val surahNomor: Int,
    val surahNamaLatin: String,
    val surahNamaArab: String,
    val nomorAyat: Int,
    val teksArabSnippet: String,
    val teksIndonesiaSnippet: String,
    val savedAtTimestamp: Long = System.currentTimeMillis()
)

@Serializable
data class LastReadItem(
    val surahNomor: Int,
    val surahNamaLatin: String,
    val surahNamaArab: String,
    val nomorAyat: Int,
    val timestamp: Long = System.currentTimeMillis()
)

data class PrayerTimeSlot(
    val name: String,
    val arabicName: String,
    val time: String,
    val isNext: Boolean = false,
    val isCurrent: Boolean = false
)

data class PrayerDashboardState(
    val cityName: String = "KOTA JAKARTA",
    val cityId: String = "58a2fc6ed39fd083f55d4182bf88826d",
    val tanggal: String = "",
    val nextPrayerName: String = "",
    val countdownText: String = "",
    val slots: List<PrayerTimeSlot> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
