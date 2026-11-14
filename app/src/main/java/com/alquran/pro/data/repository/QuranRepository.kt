package com.alquran.pro.data.repository

import android.content.Context
import com.alquran.pro.data.model.BookmarkItem
import com.alquran.pro.data.model.LastReadItem
import com.alquran.pro.data.model.SuratDetailDto
import com.alquran.pro.data.model.SuratDetailResponse
import com.alquran.pro.data.model.SuratListResponse
import com.alquran.pro.data.model.SuratSummaryDto
import com.alquran.pro.data.network.NetworkClient
import com.alquran.pro.data.storage.AppPreferences
import com.alquran.pro.data.storage.DiskCacheManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class QuranRepository(context: Context) {

    private val api = NetworkClient.equranApi
    private val diskCache = DiskCacheManager(context)
    private val prefs = AppPreferences(context)

    fun getSuratList(forceRefresh: Boolean = false): Flow<Result<List<SuratSummaryDto>>> = flow {
        val cached = diskCache.read("surat_list")
        if (!cached.isNullOrBlank()) {
            try {
                val cachedResponse = NetworkClient.json.decodeFromString<SuratListResponse>(cached)
                if (cachedResponse.data.isNotEmpty()) {
                    emit(Result.success(cachedResponse.data))
                }
            } catch (_: Exception) {
                // Ignore parse errors from corrupted cache
            }
        }

        try {
            val response = api.getSuratList()
            if (response.code == 200 && response.data.isNotEmpty()) {
                val jsonString = NetworkClient.json.encodeToString(response)
                diskCache.save("surat_list", jsonString)
                emit(Result.success(response.data))
            } else if (cached.isNullOrBlank()) {
                emit(Result.failure(Exception("Gagal memuat data surat (Kode: ${response.code})")))
            }
        } catch (e: Exception) {
            if (cached.isNullOrBlank()) {
                emit(Result.failure(e))
            }
        }
    }.flowOn(Dispatchers.IO)

    fun getSuratDetail(surahNomor: Int): Flow<Result<SuratDetailDto>> = flow {
        val cacheKey = "surat_detail_$surahNomor"
        val cached = diskCache.read(cacheKey)
        if (!cached.isNullOrBlank()) {
            try {
                val cachedResponse = NetworkClient.json.decodeFromString<SuratDetailResponse>(cached)
                val detail = cachedResponse.data
                if (detail != null) {
                    emit(Result.success(detail))
                }
            } catch (_: Exception) {
                // Ignore cache parsing exception
            }
        }

        try {
            val response = api.getSuratDetail(surahNomor)
            val detail = response.data
            if (detail != null) {
                val jsonString = NetworkClient.json.encodeToString(response)
                diskCache.save(cacheKey, jsonString)
                emit(Result.success(detail))
            } else if (cached.isNullOrBlank()) {
                emit(Result.failure(Exception("Detail surat tidak ditemukan")))
            }
        } catch (e: Exception) {
            if (cached.isNullOrBlank()) {
                emit(Result.failure(e))
            }
        }
    }.flowOn(Dispatchers.IO)

    val lastReadFlow: Flow<LastReadItem?> = prefs.lastReadFlow
    val bookmarksFlow: Flow<List<BookmarkItem>> = prefs.bookmarksFlow

    suspend fun saveLastRead(item: LastReadItem) = withContext(Dispatchers.IO) {
        prefs.saveLastRead(item)
    }

    suspend fun toggleBookmark(item: BookmarkItem) = withContext(Dispatchers.IO) {
        prefs.toggleBookmark(item)
    }
}
