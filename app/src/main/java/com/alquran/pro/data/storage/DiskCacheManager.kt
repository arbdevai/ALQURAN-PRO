package com.alquran.pro.data.storage

import android.content.Context
import java.io.File

class DiskCacheManager(private val context: Context) {

    private val cacheDir: File
        get() = File(context.filesDir, "quran_cache").apply {
            if (!exists()) mkdirs()
        }

    fun save(key: String, content: String) {
        try {
            val file = File(cacheDir, "$key.json")
            file.writeText(content, Charsets.UTF_8)
        } catch (_: Exception) {
            // Non-critical cache failure
        }
    }

    fun read(key: String): String? {
        return try {
            val file = File(cacheDir, "$key.json")
            if (file.exists() && file.length() > 0) {
                file.readText(Charsets.UTF_8)
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }
}
