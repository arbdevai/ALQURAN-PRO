package com.alquran.pro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.alquran.pro.data.model.KotaDto
import com.alquran.pro.ui.theme.DarkSurface
import com.alquran.pro.ui.theme.DarkSurfaceBorder
import com.alquran.pro.ui.theme.DarkSurfaceContainer
import com.alquran.pro.ui.theme.EmeraldPrimary
import com.alquran.pro.ui.theme.TextHighContrast
import com.alquran.pro.ui.theme.TextMuted

@Composable
fun CityPickerDialog(
    query: String,
    results: List<KotaDto>,
    isSearching: Boolean,
    onQueryChange: (String) -> Unit,
    onCitySelected: (KotaDto) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(DarkSurface)
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pilih Kota / Kabupaten",
                        color = TextHighContrast,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Tutup",
                        tint = TextMuted,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable(onClick = onDismiss)
                    )
                }

                // Input Pencarian Kota (MyQuran v3)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 12.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkSurfaceContainer)
                        .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(14.dp))
                ) {
                    TextField(
                        value = query,
                        onValueChange = onQueryChange,
                        placeholder = {
                            Text(
                                text = "Ketik nama kota (contoh: Bandung, Surabaya)...",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Cari",
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        trailingIcon = {
                            if (isSearching) {
                                CircularProgressIndicator(
                                    color = EmeraldPrimary,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = EmeraldPrimary,
                            focusedTextColor = TextHighContrast,
                            unfocusedTextColor = TextHighContrast
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Hasil Pencarian
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (results.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (query.isEmpty()) "Mulai ketik nama kota/kabupaten" else "Kota tidak ditemukan",
                                    color = TextMuted,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    } else {
                        items(results, key = { it.id }) { kota ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onCitySelected(kota) }
                                    .padding(horizontal = 12.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.LocationCity,
                                    contentDescription = "Kota",
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = kota.lokasi,
                                    color = TextHighContrast,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
