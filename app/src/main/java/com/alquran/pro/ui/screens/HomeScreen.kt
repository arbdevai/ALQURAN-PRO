package com.alquran.pro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alquran.pro.R
import com.alquran.pro.data.model.LastReadItem
import com.alquran.pro.data.model.PrayerDashboardState
import com.alquran.pro.ui.components.BentoMenuCard
import com.alquran.pro.ui.components.MainTab
import com.alquran.pro.ui.theme.DarkSurface
import com.alquran.pro.ui.theme.DarkSurfaceBorder
import com.alquran.pro.ui.theme.DarkSurfaceContainer
import com.alquran.pro.ui.theme.EmeraldContainer
import com.alquran.pro.ui.theme.EmeraldPrimary
import com.alquran.pro.ui.theme.GoldAccent
import com.alquran.pro.ui.theme.OledBlack
import com.alquran.pro.ui.theme.TextHighContrast
import com.alquran.pro.ui.theme.TextMuted

@Composable
fun HomeScreen(
    prayerDashboard: PrayerDashboardState,
    lastRead: LastReadItem?,
    onNavigateTab: (MainTab) -> Unit,
    onContinueReading: (surahNomor: Int) -> Unit,
    onOpenPrayerCityPicker: () -> Unit,
    onOpenAboutDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OledBlack),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Sambutan
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "AL-QUR'AN PRO",
                    color = EmeraldPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Assalamu'alaikum",
                    color = TextHighContrast,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    text = if (prayerDashboard.tanggal.isNotBlank()) prayerDashboard.tanggal else "Mushaf Digital & Jadwal Sholat",
                    color = TextMuted,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        // 2. Hero Bento Card: Sholat Terdekat (MyQuran v3)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(DarkSurface)
                    .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(22.dp))
                    .clickable { onNavigateTab(MainTab.PRAYER) }
                    .padding(20.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onOpenPrayerCityPicker() }
                                .padding(vertical = 2.dp, horizontal = 4.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_lucide_map_pin),
                                contentDescription = "Lokasi",
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = prayerDashboard.cityName,
                                color = TextHighContrast,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(EmeraldContainer)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "JADWAL SHOLAT",
                                color = TextHighContrast,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
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
                                text = prayerDashboard.nextPrayerName.ifBlank { "Menghitung..." },
                                color = TextHighContrast,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                            Text(
                                text = prayerDashboard.countdownText.ifBlank { "Memuat jadwal..." },
                                color = EmeraldPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(DarkSurfaceContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_lucide_clock),
                                contentDescription = "Waktu Sholat",
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }
            }
        }

        // 3. Kartu Terakhir Dibaca (Quick Resume)
        if (lastRead != null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(DarkSurface)
                        .border(1.dp, EmeraldPrimary, RoundedCornerShape(18.dp))
                        .clickable { onContinueReading(lastRead.surahNomor) }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(DarkSurfaceContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_lucide_history),
                                    contentDescription = "Riwayat Bacaan",
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "TERAKHIR DIBACA",
                                    color = EmeraldPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = lastRead.surahNamaLatin,
                                    color = TextHighContrast,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                                Text(
                                    text = "Ayat ke-${lastRead.nomorAyat}",
                                    color = GoldAccent,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(EmeraldContainer)
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Lanjutkan",
                                color = TextHighContrast,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // 4. Section Label: Menu Utama
        item {
            Text(
                text = "MENU UTAMA",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // 5. Bento Grid Kotak-kotak Kompak (Baris 1)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BentoMenuCard(
                    title = "Al-Qur'an",
                    subtitle = "114 Surat & Audio",
                    iconRes = R.drawable.ic_lucide_book_open,
                    accentColor = EmeraldPrimary,
                    onClick = { onNavigateTab(MainTab.QURAN) },
                    modifier = Modifier.weight(1f)
                )

                BentoMenuCard(
                    title = "Jadwal Sholat",
                    subtitle = "8 Waktu & Imsak",
                    iconRes = R.drawable.ic_lucide_clock,
                    accentColor = GoldAccent,
                    onClick = { onNavigateTab(MainTab.PRAYER) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 6. Bento Grid Kotak-kotak Kompak (Baris 2)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BentoMenuCard(
                    title = "Tersimpan",
                    subtitle = "Bookmark Ayat",
                    iconRes = R.drawable.ic_lucide_bookmark,
                    accentColor = GoldAccent,
                    onClick = { onNavigateTab(MainTab.BOOKMARKS) },
                    modifier = Modifier.weight(1f)
                )

                BentoMenuCard(
                    title = "Info & Update",
                    subtitle = "Sahal Arbani • API",
                    iconRes = R.drawable.ic_lucide_info,
                    accentColor = EmeraldPrimary,
                    onClick = { onOpenAboutDialog() },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
