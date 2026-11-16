package com.alquran.pro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alquran.pro.R
import com.alquran.pro.data.model.PrayerDashboardState
import com.alquran.pro.data.model.PrayerTimeSlot
import com.alquran.pro.ui.theme.DarkSurface
import com.alquran.pro.ui.theme.DarkSurfaceBorder
import com.alquran.pro.ui.theme.DarkSurfaceContainer
import com.alquran.pro.ui.theme.EmeraldContainer
import com.alquran.pro.ui.theme.EmeraldPrimary
import com.alquran.pro.ui.theme.GoldAccent
import com.alquran.pro.ui.theme.TextArabic
import com.alquran.pro.ui.theme.TextHighContrast
import com.alquran.pro.ui.theme.TextMediumContrast
import com.alquran.pro.ui.theme.TextMuted

/**
 * Kartu Tunggal Terpadu Jadwal Sholat Hari Ini.
 * Menggabungkan deteksi lokasi, hitung mundur, dan jadwal 8 waktu dalam 1 wadah rapi efisien.
 */
@Composable
fun UnifiedPrayerScheduleCard(
    state: PrayerDashboardState,
    isDetectingLocation: Boolean,
    locationMessage: String?,
    onAutoDetectLocationClick: () -> Unit,
    onChangeCityClick: () -> Unit,
    onRefreshClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(DarkSurface)
            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(22.dp))
            .padding(18.dp)
    ) {
        // 1. Bar Lokasi & Aksi (Deteksi GPS, Ganti Kota, Refresh)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Chip Nama Kota
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f, fill = false)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(onClick = onChangeCityClick)
                    .padding(vertical = 4.dp, horizontal = 4.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_lucide_map_pin),
                    contentDescription = "Lokasi Kota",
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = state.cityName,
                    color = TextHighContrast,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    modifier = Modifier.padding(start = 6.dp, end = 6.dp)
                )
                Icon(
                    imageVector = Icons.Filled.SwapHoriz,
                    contentDescription = "Ganti Kota",
                    tint = TextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Tombol Aksi GPS & Refresh
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Tombol GPS Auto Detect
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(if (isDetectingLocation) EmeraldContainer else DarkSurfaceContainer)
                        .clickable(enabled = !isDetectingLocation, onClick = onAutoDetectLocationClick),
                    contentAlignment = Alignment.Center
                ) {
                    if (isDetectingLocation) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = EmeraldPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.ic_lucide_crosshair),
                            contentDescription = "Deteksi Lokasi GPS Otomatis",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Tombol Refresh
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceContainer)
                        .clickable(onClick = onRefreshClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Muat Ulang Jadwal",
                        tint = TextMediumContrast,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Pesan Status Deteksi Lokasi (Jika ada)
        if (!locationMessage.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkSurfaceContainer)
                    .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = locationMessage,
                    color = EmeraldPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Tanggal
        if (state.tanggal.isNotBlank()) {
            Text(
                text = state.tanggal,
                color = TextMuted,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // 2. Sub-Hero Menuju Sholat Berikutnya
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurfaceContainer)
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MENUJU SHOLAT",
                        color = GoldAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = state.nextPrayerName.ifBlank { "Menghitung..." },
                        color = TextHighContrast,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Text(
                        text = state.countdownText,
                        color = EmeraldPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(EmeraldContainer)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "AKTIF",
                        color = TextHighContrast,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // 3. Garis Pemisah Halus
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 14.dp),
            thickness = 1.dp,
            color = DarkSurfaceBorder
        )

        // 4. Header Judul Sub-bagian
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "JADWAL SHOLAT HARI INI",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = "8 Waktu",
                color = TextMuted,
                fontSize = 11.sp
            )
        }

        // 5. Daftar 8 Waktu Sholat di dalam Satu Card yang Sama
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            state.slots.forEach { slot ->
                UnifiedPrayerSlotRow(slot = slot)
            }
        }
    }
}

@Composable
fun UnifiedPrayerSlotRow(
    slot: PrayerTimeSlot,
    modifier: Modifier = Modifier
) {
    val isHighlighted = slot.isNext
    val rowBg = if (isHighlighted) EmeraldContainer.copy(alpha = 0.55f) else DarkSurfaceContainer.copy(alpha = 0.45f)
    val rowBorder = if (isHighlighted) EmeraldPrimary else Color.Transparent
    val timeColor = if (isHighlighted) TextHighContrast else if (slot.isCurrent) GoldAccent else TextHighContrast
    val nameColor = if (isHighlighted) TextHighContrast else TextMediumContrast

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(rowBg)
            .border(1.dp, rowBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        if (isHighlighted) EmeraldPrimary
                        else if (slot.isCurrent) GoldAccent
                        else DarkSurfaceBorder
                    )
            )

            Column {
                Text(
                    text = slot.name,
                    color = nameColor,
                    fontSize = 14.sp,
                    fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Medium
                )
                if (isHighlighted) {
                    Text(
                        text = "Berikutnya",
                        color = EmeraldPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = slot.arabicName,
                color = TextArabic,
                fontFamily = FontFamily.Serif,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = slot.time,
                color = timeColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
