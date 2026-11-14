package com.alquran.pro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun PrayerCountdownHero(
    state: PrayerDashboardState,
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
            .padding(20.dp)
    ) {
        // Bar Lokasi & Tombol Ganti Kota
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(onClick = onChangeCityClick)
                    .padding(vertical = 4.dp, horizontal = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = "Lokasi Kota",
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = state.cityName,
                    color = TextHighContrast,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 6.dp, end = 6.dp)
                )
                Icon(
                    imageVector = Icons.Filled.SwapHoriz,
                    contentDescription = "Ganti Kota",
                    tint = TextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }

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

        // Tanggal
        if (state.tanggal.isNotBlank()) {
            Text(
                text = state.tanggal,
                color = TextMuted,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Hero Info Waktu Sholat Berikutnya
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurfaceContainer)
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp))
                .padding(18.dp)
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
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Text(
                        text = state.countdownText,
                        color = EmeraldPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Badge Indikator
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
    }
}

@Composable
fun PrayerTimeRow(
    slot: PrayerTimeSlot,
    modifier: Modifier = Modifier
) {
    val isHighlighted = slot.isNext
    val cardBg = if (isHighlighted) EmeraldContainer else DarkSurface
    val borderColor = if (isHighlighted) EmeraldPrimary else DarkSurfaceBorder
    val timeColor = if (isHighlighted) TextHighContrast else if (slot.isCurrent) GoldAccent else TextHighContrast
    val nameColor = if (isHighlighted) TextHighContrast else TextMediumContrast

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(cardBg)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isHighlighted) EmeraldPrimary else if (slot.isCurrent) GoldAccent else DarkSurfaceBorder)
            )

            Column {
                Text(
                    text = slot.name,
                    color = nameColor,
                    fontSize = 15.sp,
                    fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Medium
                )
                if (isHighlighted) {
                    Text(
                        text = "Sholat Berikutnya",
                        color = EmeraldPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = slot.arabicName,
                color = TextArabic,
                fontFamily = FontFamily.Serif,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = slot.time,
                color = timeColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
