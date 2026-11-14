package com.alquran.pro.data.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.alquran.pro.data.model.BookmarkItem
import com.alquran.pro.data.model.LastReadItem
import com.alquran.pro.data.network.NetworkClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "alquran_pro_prefs")

class AppPreferences(private val context: Context) {

    private object Keys {
        val KEY_CITY_ID = stringPreferencesKey("city_id")
        val KEY_CITY_NAME = stringPreferencesKey("city_name")
        val KEY_LAST_READ = stringPreferencesKey("last_read")
        val KEY_BOOKMARKS = stringPreferencesKey("bookmarks_json")
    }

    val cityIdFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.KEY_CITY_ID] ?: "58a2fc6ed39fd083f55d4182bf88826d"
    }

    val cityNameFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.KEY_CITY_NAME] ?: "KOTA JAKARTA"
    }

    suspend fun saveSelectedCity(id: String, name: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.KEY_CITY_ID] = id
            prefs[Keys.KEY_CITY_NAME] = name
        }
    }

    val lastReadFlow: Flow<LastReadItem?> = context.dataStore.data.map { prefs ->
        val jsonStr = prefs[Keys.KEY_LAST_READ] ?: return@map null
        try {
            NetworkClient.json.decodeFromString<LastReadItem>(jsonStr)
        } catch (_: Exception) {
            null
        }
    }

    suspend fun saveLastRead(item: LastReadItem) {
        context.dataStore.edit { prefs ->
            val jsonStr = NetworkClient.json.encodeToString(item)
            prefs[Keys.KEY_LAST_READ] = jsonStr
        }
    }

    val bookmarksFlow: Flow<List<BookmarkItem>> = context.dataStore.data.map { prefs ->
        val jsonStr = prefs[Keys.KEY_BOOKMARKS] ?: return@map emptyList()
        try {
            NetworkClient.json.decodeFromString<List<BookmarkItem>>(jsonStr)
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun toggleBookmark(item: BookmarkItem) {
        context.dataStore.edit { prefs ->
            val currentJson = prefs[Keys.KEY_BOOKMARKS]
            val currentList = if (!currentJson.isNullOrBlank()) {
                try {
                    NetworkClient.json.decodeFromString<List<BookmarkItem>>(currentJson)
                } catch (_: Exception) {
                    emptyList()
                }
            } else {
                emptyList()
            }

            val exists = currentList.any { it.surahNomor == item.surahNomor && it.nomorAyat == item.nomorAyat }
            val updatedList = if (exists) {
                currentList.filterNot { it.surahNomor == item.surahNomor && it.nomorAyat == item.nomorAyat }
            } else {
                listOf(item) + currentList
            }

            prefs[Keys.KEY_BOOKMARKS] = NetworkClient.json.encodeToString(updatedList)
        }
    }
}
