package com.alquran.pro.updater

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import com.alquran.pro.BuildConfig
import com.alquran.pro.data.model.GitHubReleaseDto
import com.alquran.pro.data.network.NetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

sealed class UpdateCheckResult {
    data object UpToDate : UpdateCheckResult()
    data class UpdateAvailable(
        val newVersion: String,
        val releaseNotes: String,
        val downloadUrl: String,
        val fileSizeBytes: Long
    ) : UpdateCheckResult()
}

class InAppUpdateManager(private val context: Context) {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    private val updatesDir: File
        get() = File(context.cacheDir, "app_updates").apply {
            if (!exists()) mkdirs()
        }

    suspend fun checkForUpdates(): Result<UpdateCheckResult> = withContext(Dispatchers.IO) {
        try {
            val url = "https://api.github.com/repos/arbdevai/ALQURAN-PRO/releases/latest"
            val request = Request.Builder()
                .url(url)
                .header("Accept", "application/vnd.github.v3+json")
                .header("User-Agent", "AlQuranPro-Android")
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Gagal memeriksa pembaruan (Kode: ${response.code})"))
            }

            val bodyString = response.body?.string()
                ?: return@withContext Result.failure(Exception("Respons server kosong"))

            val release = NetworkClient.json.decodeFromString<GitHubReleaseDto>(bodyString)
            val remoteTag = release.tagName.trim().removePrefix("v")
            val currentVersion = BuildConfig.VERSION_NAME.trim().removePrefix("v")

            if (!isRemoteVersionNewer(remoteTag, currentVersion)) {
                return@withContext Result.success(UpdateCheckResult.UpToDate)
            }

            // Pilih APK sesuai arsitektur CPU perangkat (Build.SUPPORTED_ABIS)
            val isArm64 = Build.SUPPORTED_ABIS.any { it.contains("arm64", ignoreCase = true) }
            val preferredAbi = if (isArm64) "arm64-v8a" else "armeabi-v7a"

            val matchingAsset = release.assets.firstOrNull { it.name.contains(preferredAbi, ignoreCase = true) }
                ?: release.assets.firstOrNull { it.name.endsWith(".apk", ignoreCase = true) }
                ?: return@withContext Result.failure(Exception("Berkas instalasi untuk CPU ini tidak ditemukan"))

            Result.success(
                UpdateCheckResult.UpdateAvailable(
                    newVersion = release.tagName,
                    releaseNotes = release.body,
                    downloadUrl = matchingAsset.downloadUrl,
                    fileSizeBytes = matchingAsset.size
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun downloadApk(
        downloadUrl: String,
        expectedSizeBytes: Long,
        onProgress: (percent: Int, downloaded: Long, total: Long) -> Unit
    ): Result<File> = withContext(Dispatchers.IO) {
        // Bersihkan unduhan lama terlebih dahulu
        cleanupOldUpdates()

        val partFile = File(updatesDir, "update_download.apk.part")
        val finalApk = File(updatesDir, "update_release.apk")

        try {
            val request = Request.Builder()
                .url(downloadUrl)
                .header("User-Agent", "AlQuranPro-Android")
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Gagal mengunduh berkas (Kode: ${response.code})"))
            }

            val body = response.body ?: return@withContext Result.failure(Exception("Badan berkas unduhan kosong"))
            val totalBytes = if (expectedSizeBytes > 0) expectedSizeBytes else body.contentLength()

            body.byteStream().use { inputStream ->
                FileOutputStream(partFile).use { outputStream ->
                    val buffer = ByteArray(64 * 1024)
                    var bytesRead: Int
                    var accumulatedBytes = 0L

                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                        accumulatedBytes += bytesRead

                        val percent = if (totalBytes > 0) {
                            ((accumulatedBytes * 100) / totalBytes).toInt().coerceIn(0, 100)
                        } else {
                            0
                        }
                        onProgress(percent, accumulatedBytes, totalBytes)
                    }
                    outputStream.flush()
                }
            }

            // Verifikasi integritas 4 lapis
            if (!isApkValid(context, partFile, totalBytes)) {
                partFile.delete()
                return@withContext Result.failure(Exception("Verifikasi integritas APK gagal. Paket rusak atau tidak cocok."))
            }

            if (finalApk.exists()) finalApk.delete()
            if (!partFile.renameTo(finalApk)) {
                return@withContext Result.failure(Exception("Gagal menyiapkan berkas instalasi akhir"))
            }

            Result.success(finalApk)
        } catch (e: Exception) {
            if (partFile.exists()) partFile.delete()
            Result.failure(e)
        }
    }

    fun installApk(apkFile: File) {
        if (!apkFile.exists() || apkFile.length() < 5_000_000) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !context.packageManager.canRequestPackageInstalls()) {
            val settingsIntent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                data = Uri.parse("package:${context.packageName}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(settingsIntent)
            return
        }

        val authority = "${context.packageName}.provider"
        val contentUri = FileProvider.getUriForFile(context, authority, apkFile)
        val installIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(contentUri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(installIntent)
    }

    private fun isApkValid(context: Context, apkFile: File, expectedBytes: Long): Boolean {
        if (!apkFile.exists() || !apkFile.isFile || apkFile.length() < 10_000_000) return false
        if (expectedBytes > 0L && Math.abs(apkFile.length() - expectedBytes) > 2048L) return false

        return try {
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                PackageManager.GET_SIGNING_CERTIFICATES or PackageManager.GET_ACTIVITIES
            } else {
                @Suppress("DEPRECATION")
                PackageManager.GET_SIGNATURES or PackageManager.GET_ACTIVITIES
            }

            val pkg = context.packageManager.getPackageArchiveInfo(apkFile.absolutePath, flags) ?: return false
            pkg.packageName == context.packageName
        } catch (_: Exception) {
            false
        }
    }

    private fun cleanupOldUpdates() {
        try {
            updatesDir.listFiles()?.forEach { file ->
                if (file.isFile) file.delete()
            }
        } catch (_: Exception) {
            // Ignore cleanup failure
        }
    }

    private fun isRemoteVersionNewer(remoteVer: String, currentVer: String): Boolean {
        val remoteParts = remoteVer.split(".").mapNotNull { it.toIntOrNull() }
        val currentParts = currentVer.split(".").mapNotNull { it.toIntOrNull() }

        val length = maxOf(remoteParts.size, currentParts.size)
        for (i in 0 until length) {
            val r = remoteParts.getOrElse(i) { 0 }
            val c = currentParts.getOrElse(i) { 0 }
            if (r > c) return true
            if (r < c) return false
        }
        return false
    }
}
