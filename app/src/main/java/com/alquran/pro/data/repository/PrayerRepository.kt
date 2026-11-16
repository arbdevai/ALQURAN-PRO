package com.alquran.pro.data.repository

import android.content.Context
import com.alquran.pro.data.location.LocationHelper
import com.alquran.pro.data.model.JadwalResponse
import com.alquran.pro.data.model.KotaDto
import com.alquran.pro.data.model.KotaListResponse
import com.alquran.pro.data.model.PrayerDashboardState
import com.alquran.pro.data.model.PrayerTimeSlot
import com.alquran.pro.data.network.NetworkClient
import com.alquran.pro.data.storage.AppPreferences
import com.alquran.pro.data.storage.DiskCacheManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.Calendar

class PrayerRepository(context: Context) {

    private val api = NetworkClient.myquranApi
    private val diskCache = DiskCacheManager(context)
    private val prefs = AppPreferences(context)

    val selectedCityIdFlow: Flow<String> = prefs.cityIdFlow
    val selectedCityNameFlow: Flow<String> = prefs.cityNameFlow

    suspend fun saveSelectedCity(id: String, name: String) = withContext(Dispatchers.IO) {
        prefs.saveSelectedCity(id, name)
    }

    fun getPrayerDashboard(cityId: String, cityName: String): Flow<Result<PrayerDashboardState>> = flow {
        val cacheKey = "prayer_schedule_$cityId"
        val cached = diskCache.read(cacheKey)
        if (!cached.isNullOrBlank()) {
            try {
                val cachedResponse = NetworkClient.json.decodeFromString<JadwalResponse>(cached)
                val state = parseJadwalToDashboard(cityId, cityName, cachedResponse)
                if (state != null) {
                    emit(Result.success(state))
                }
            } catch (_: Exception) {
                // Ignore parse errors from stale cache
            }
        }

        try {
            val response = api.getJadwalToday(cityId)
            if (response.status && response.data != null) {
                val jsonString = NetworkClient.json.encodeToString(response)
                diskCache.save(cacheKey, jsonString)
                val parsed = parseJadwalToDashboard(cityId, cityName, response)
                if (parsed != null) {
                    emit(Result.success(parsed))
                } else {
                    emit(Result.failure(Exception("Format jadwal sholat tidak valid")))
                }
            } else if (cached.isNullOrBlank()) {
                emit(Result.failure(Exception(response.message.ifBlank { "Gagal memuat jadwal sholat" })))
            }
        } catch (e: Exception) {
            if (cached.isNullOrBlank()) {
                emit(Result.failure(e))
            }
        }
    }.flowOn(Dispatchers.IO)

    fun searchCities(query: String): Flow<Result<List<KotaDto>>> = flow {
        if (query.trim().isEmpty()) {
            emit(Result.success(emptyList()))
            return@flow
        }
        try {
            val response = api.searchKota(query.trim())
            if (response.status) {
                emit(Result.success(response.data))
            } else {
                emit(Result.success(emptyList()))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun findCityByLocation(context: Context): Result<KotaDto> = withContext(Dispatchers.IO) {
        if (!LocationHelper.hasLocationPermission(context)) {
            return@withContext Result.failure(Exception("Izin lokasi diperlukan untuk deteksi otomatis"))
        }

        val location = LocationHelper.getCurrentLocation(context)
            ?: return@withContext Result.failure(Exception("Gagal mendeteksi koordinat GPS. Pastikan sensor lokasi aktif."))

        val candidates = LocationHelper.resolveCitySearchCandidates(context, location)

        for (candidate in candidates) {
            try {
                val response = api.searchKota(candidate)
                if (response.status && response.data.isNotEmpty()) {
                    val exactMatch = response.data.firstOrNull {
                        it.lokasi.contains(candidate, ignoreCase = true)
                    } ?: response.data.first()
                    return@withContext Result.success(exactMatch)
                }
            } catch (_: Exception) {
            }
        }

        val fallback = LocationHelper.findNearestMajorCity(location.latitude, location.longitude)
        try {
            val response = api.searchKota(fallback.name)
            if (response.status && response.data.isNotEmpty()) {
                return@withContext Result.success(response.data.first())
            }
        } catch (_: Exception) {
        }

        Result.failure(Exception("Lokasi terdekat belum tersedia dalam basis data MyQuran"))
    }

    private fun parseJadwalToDashboard(
        cityId: String,
        cityName: String,
        response: JadwalResponse
    ): PrayerDashboardState? {
        val container = response.data ?: return null
        val jadwalMap = container.jadwal
        val todayEntry = jadwalMap.values.firstOrNull() ?: return null

        val now = Calendar.getInstance()
        val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)

        val rawSlots = listOf(
            RawSlot("Imsak", "الإمساك", todayEntry.imsak),
            RawSlot("Subuh", "الفجر", todayEntry.subuh),
            RawSlot("Terbit", "الشروق", todayEntry.terbit),
            RawSlot("Dhuha", "الضحى", todayEntry.dhuha),
            RawSlot("Dzuhur", "الظهر", todayEntry.dzuhur),
            RawSlot("Ashar", "العصر", todayEntry.ashar),
            RawSlot("Maghrib", "المغرب", todayEntry.maghrib),
            RawSlot("Isya", "العشاء", todayEntry.isya)
        )

        // Cari waktu sholat berikutnya
        var nextSlot: RawSlot? = null
        var diffMinutes = 0

        for (slot in rawSlots) {
            val slotMinutes = parseTimeToMinutes(slot.time)
            if (slotMinutes > currentMinutes) {
                nextSlot = slot
                diffMinutes = slotMinutes - currentMinutes
                break
            }
        }

        // Jika semua sholat hari ini telah lewat, waktu berikutnya adalah Subuh besok
        val isTomorrow = nextSlot == null
        if (nextSlot == null) {
            nextSlot = rawSlots.firstOrNull { it.name == "Subuh" } ?: rawSlots.first()
            val tomorrowSubuhMinutes = parseTimeToMinutes(nextSlot.time) + (24 * 60)
            diffMinutes = tomorrowSubuhMinutes - currentMinutes
        }

        val countdownText = if (diffMinutes > 0) {
            val hours = diffMinutes / 60
            val mins = diffMinutes % 60
            if (hours > 0) "$hours jam $mins menit lagi" else "$mins menit lagi"
        } else {
            "Waktu Sholat Telah Tiba"
        }

        val finalSlots = rawSlots.map { slot ->
            val isNext = (!isTomorrow && slot.name == nextSlot?.name)
            val isPassed = parseTimeToMinutes(slot.time) <= currentMinutes
            PrayerTimeSlot(
                name = slot.name,
                arabicName = slot.arabicName,
                time = slot.time,
                isNext = isNext,
                isCurrent = isPassed && !isNext
            )
        }

        val displayedCity = if (container.kabko.isNotBlank()) container.kabko else cityName

        return PrayerDashboardState(
            cityName = displayedCity,
            cityId = cityId,
            tanggal = todayEntry.tanggal,
            nextPrayerName = nextSlot.name,
            countdownText = countdownText,
            slots = finalSlots,
            isLoading = false,
            errorMessage = null
        )
    }

    private fun parseTimeToMinutes(timeStr: String): Int {
        val parts = timeStr.trim().split(":")
        if (parts.size >= 2) {
            val h = parts[0].toIntOrNull() ?: 0
            val m = parts[1].toIntOrNull() ?: 0
            return h * 60 + m
        }
        return 0
    }

    private data class RawSlot(val name: String, val arabicName: String, val time: String)
}
