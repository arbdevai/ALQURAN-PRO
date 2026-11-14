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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alquran.pro.data.model.AyatDto
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
fun AyahCard(
    ayat: AyatDto,
    isBookmarked: Boolean,
    isPlaying: Boolean,
    onPlayAudioClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onMarkLastReadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(DarkSurface)
            .border(1.dp, if (isPlaying) EmeraldPrimary else DarkSurfaceBorder, RoundedCornerShape(18.dp))
            .padding(18.dp)
    ) {
        // Bar Aksi Header Ayat
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Nomor Ayat Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkSurfaceContainer)
                    .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Ayat ${ayat.nomorAyat}",
                    color = EmeraldPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Tombol Aksi (Play Audio, Bookmark, Set Terakhir Dibaca)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Audio Play/Pause Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isPlaying) EmeraldContainer else DarkSurfaceContainer)
                        .clickable(onClick = onPlayAudioClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = "Putar Audio Ayat",
                        tint = if (isPlaying) EmeraldPrimary else TextMediumContrast,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Bookmark Toggle Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceContainer)
                        .clickable(onClick = onBookmarkClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                        contentDescription = "Simpan Bookmark",
                        tint = if (isBookmarked) GoldAccent else TextMediumContrast,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Tandai Terakhir Dibaca
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceContainer)
                        .clickable(onClick = onMarkLastReadClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.History,
                        contentDescription = "Tandai Terakhir Dibaca",
                        tint = TextMediumContrast,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Teks Arab Ayat (Right-to-Left, Ukuran Proporsional & Elegan)
        Text(
            text = ayat.teksArab,
            color = TextArabic,
            fontSize = 26.sp,
            fontFamily = FontFamily.Serif,
            lineHeight = 48.sp,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp, bottom = 14.dp)
        )

        // Transliterasi Latin
        Text(
            text = ayat.teksLatin,
            color = EmeraldPrimary,
            fontSize = 13.sp,
            fontStyle = FontStyle.Italic,
            lineHeight = 20.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Terjemahan Bahasa Indonesia
        Text(
            text = ayat.teksIndonesia,
            color = TextMediumContrast,
            fontSize = 14.sp,
            lineHeight = 22.sp
        )
    }
}
