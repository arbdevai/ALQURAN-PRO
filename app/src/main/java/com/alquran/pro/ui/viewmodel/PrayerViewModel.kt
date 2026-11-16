package com.alquran.pro.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.alquran.pro.data.model.KotaDto
import com.alquran.pro.data.model.PrayerDashboardState
import com.alquran.pro.data.repository.PrayerRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PrayerUiState(
    val dashboard: PrayerDashboardState = PrayerDashboardState(),
    val citySearchQuery: String = "",
    val searchResults: List<KotaDto> = emptyList(),
    val isSearching: Boolean = false,
    val showCityDialog: Boolean = false,
    val isDetectingLocation: Boolean = false,
    val locationMessage: String? = null
)

class PrayerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PrayerRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(PrayerUiState())
    val uiState: StateFlow<PrayerUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadSchedule()
    }

    fun loadSchedule() {
        viewModelScope.launch {
            val cityId = repository.selectedCityIdFlow.first()
            val cityName = repository.selectedCityNameFlow.first()

            _uiState.update {
                it.copy(
                    dashboard = it.dashboard.copy(
                        isLoading = true,
                        errorMessage = null,
                        cityId = cityId,
                        cityName = cityName
                    )
                )
            }

            repository.getPrayerDashboard(cityId, cityName).collect { result ->
                result.fold(
                    onSuccess = { state ->
                        _uiState.update { current -> current.copy(dashboard = state) }
                    },
                    onFailure = { err ->
                        _uiState.update { current ->
                            current.copy(
                                dashboard = current.dashboard.copy(
                                    isLoading = false,
                                    errorMessage = err.message ?: "Gagal memuat jadwal"
                                )
                            )
                        }
                    }
                )
            }
        }
    }

    fun onCityQueryChange(query: String) {
        _uiState.update { it.copy(citySearchQuery = query) }
        searchJob?.cancel()

        if (query.trim().isEmpty()) {
            _uiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
            return
        }

        searchJob = viewModelScope.launch {
            delay(400) // Debounce pencarian
            _uiState.update { it.copy(isSearching = true) }
            repository.searchCities(query).collect { result ->
                result.fold(
                    onSuccess = { list ->
                        _uiState.update { it.copy(searchResults = list, isSearching = false) }
                    },
                    onFailure = {
                        _uiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
                    }
                )
            }
        }
    }

    fun selectCity(kota: KotaDto) {
        viewModelScope.launch {
            repository.saveSelectedCity(kota.id, kota.lokasi)
            _uiState.update {
                it.copy(
                    showCityDialog = false,
                    citySearchQuery = "",
                    searchResults = emptyList()
                )
            }
            loadSchedule()
        }
    }

    fun openCityDialog() {
        _uiState.update { it.copy(showCityDialog = true, citySearchQuery = "", searchResults = emptyList()) }
    }

    fun closeCityDialog() {
        _uiState.update { it.copy(showCityDialog = false) }
    }

    fun autoDetectLocation() {
        viewModelScope.launch {
            _uiState.update { it.copy(isDetectingLocation = true, locationMessage = "Mencari lokasi GPS terdekat...") }
            repository.findCityByLocation(getApplication<Application>().applicationContext).fold(
                onSuccess = { kota ->
                    _uiState.update { current ->
                        current.copy(
                            isDetectingLocation = false,
                            locationMessage = "Lokasi terdeteksi: ${kota.lokasi}"
                        )
                    }
                    selectCity(kota)
                },
                onFailure = { err ->
                    _uiState.update { current ->
                        current.copy(
                            isDetectingLocation = false,
                            locationMessage = err.message ?: "Gagal mendeteksi lokasi"
                        )
                    }
                }
            )
        }
    }

    fun clearLocationMessage() {
        _uiState.update { it.copy(locationMessage = null) }
    }
}
