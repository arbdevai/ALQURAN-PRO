package com.alquran.pro.updater

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.alquran.pro.BuildConfig
import com.alquran.pro.data.model.UpdateUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class UpdateViewModel(application: Application) : AndroidViewModel(application) {

    private val updateManager = InAppUpdateManager(application.applicationContext)

    private val _uiState = MutableStateFlow<UpdateUiState>(UpdateUiState.Idle)
    val uiState: StateFlow<UpdateUiState> = _uiState.asStateFlow()

    fun checkForUpdates() {
        viewModelScope.launch {
            _uiState.value = UpdateUiState.Checking
            val result = updateManager.checkForUpdates()
            result.fold(
                onSuccess = { checkResult ->
                    when (checkResult) {
                        is UpdateCheckResult.UpToDate -> {
                            _uiState.value = UpdateUiState.UpToDate(BuildConfig.VERSION_NAME)
                        }
                        is UpdateCheckResult.UpdateAvailable -> {
                            _uiState.value = UpdateUiState.Available(
                                latestTag = checkResult.newVersion,
                                releaseNotes = checkResult.releaseNotes,
                                assetUrl = checkResult.downloadUrl,
                                assetSize = checkResult.fileSizeBytes
                            )
                        }
                    }
                },
                onFailure = { err ->
                    _uiState.value = UpdateUiState.Error(err.message ?: "Gagal memeriksa pembaruan")
                }
            )
        }
    }

    fun startDownload(available: UpdateUiState.Available) {
        viewModelScope.launch {
            _uiState.value = UpdateUiState.Downloading(
                progressPercent = 0,
                downloadedBytes = 0L,
                totalBytes = available.assetSize
            )

            val result = updateManager.downloadApk(
                downloadUrl = available.assetUrl,
                expectedSizeBytes = available.assetSize,
                onProgress = { percent, downloaded, total ->
                    _uiState.value = UpdateUiState.Downloading(
                        progressPercent = percent,
                        downloadedBytes = downloaded,
                        totalBytes = total
                    )
                }
            )

            result.fold(
                onSuccess = { file ->
                    _uiState.value = UpdateUiState.ReadyToInstall(file.absolutePath)
                },
                onFailure = { err ->
                    _uiState.value = UpdateUiState.Error(err.message ?: "Gagal mengunduh berkas pembaruan")
                }
            )
        }
    }

    fun installApk(ready: UpdateUiState.ReadyToInstall) {
        val file = File(ready.apkPath)
        updateManager.installApk(file)
    }

    fun resetState() {
        _uiState.value = UpdateUiState.Idle
    }
}
