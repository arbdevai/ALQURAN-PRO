package com.alquran.pro.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alquran.pro.data.location.LocationHelper
import com.alquran.pro.data.model.KotaDto
import com.alquran.pro.data.model.PrayerDashboardState
import com.alquran.pro.ui.components.CityPickerDialog
import com.alquran.pro.ui.components.SkeletonBox
import com.alquran.pro.ui.components.UnifiedPrayerScheduleCard
import com.alquran.pro.ui.theme.EmeraldPrimary
import com.alquran.pro.ui.theme.OledBlack
import com.alquran.pro.ui.theme.TextHighContrast
import com.alquran.pro.ui.theme.TextMuted

@Composable
fun PrayerTimesScreen(
    state: PrayerDashboardState,
    showCityDialog: Boolean,
    citySearchQuery: String,
    citySearchResults: List<KotaDto>,
    isSearchingCity: Boolean,
    isDetectingLocation: Boolean,
    locationMessage: String?,
    onRefresh: () -> Unit,
    onAutoDetectLocation: () -> Unit,
    onOpenCityDialog: () -> Unit,
    onCloseCityDialog: () -> Unit,
    onCitySearchQueryChange: (String) -> Unit,
    onCitySelected: (KotaDto) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            onAutoDetectLocation()
        }
    }

    val handleAutoDetectClick = {
        if (LocationHelper.hasLocationPermission(context)) {
            onAutoDetectLocation()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(OledBlack)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "JADWAL SHOLAT NASIONAL",
                    color = EmeraldPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Waktu Sholat & Imsakiyah",
                    color = TextHighContrast,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    text = "Kemenag RI • Bimas Islam • MyQuran v3",
                    color = TextMuted,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            if (state.isLoading && state.slots.isEmpty()) {
                // Skeleton Loading: Wadah kartu tunggal
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SkeletonBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .size(520.dp),
                        shape = RoundedCornerShape(22.dp)
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 6.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Satu Kartu Tunggal untuk Seluruh Jadwal Hari Ini
                    item {
                        UnifiedPrayerScheduleCard(
                            state = state,
                            isDetectingLocation = isDetectingLocation,
                            locationMessage = locationMessage,
                            onAutoDetectLocationClick = handleAutoDetectClick,
                            onChangeCityClick = onOpenCityDialog,
                            onRefreshClick = onRefresh
                        )
                    }
                }
            }
        }

        // Dialog Pencarian Kota Manual
        if (showCityDialog) {
            CityPickerDialog(
                query = citySearchQuery,
                results = citySearchResults,
                isSearching = isSearchingCity,
                onQueryChange = onCitySearchQueryChange,
                onCitySelected = onCitySelected,
                onDismiss = onCloseCityDialog
            )
        }
    }
}
