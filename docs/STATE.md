# Project State & Session Memory

## Status Saat Ini
- **Fase Aktif:** Selesai (Release v1.0.0 Sukses Diterbitkan)
- **Versi Terakhir:** v1.0.0 (versionCode: 1)
- **Status CI Terakhir:** Hijau (Build signed release APK & GitHub Release Publik Berhasil)
- **Tautan Rilis:** https://github.com/arbdevai/ALQURAN-PRO/releases/tag/v1.0.0

## Fitur yang Selesai Tuntas (Vertical Slices)
- [x] Inisiasi Proyek Android Compose (Single Activity, Gradle Catalog, Android 15/16 Ready).
- [x] Konfigurasi CI/CD GitHub Actions (`build-apk.yml`) dengan Auto-Release & Signing.
- [x] Dokumen PRD dan Kontrak Desain (True OLED Dark, Zero On-screen Back Button, Zero AI Slop).
- [x] DTO & Model Data EQuran v2 (`SuratSummaryDto`, `SuratDetailDto`, `AyatDto`, `TafsirDto`).
- [x] DTO & Model Data MyQuran v3 (`KotaDto`, `JadwalResponse`, `JadwalHariIniDto`).
- [x] Network Layer (Retrofit, OkHttp, Kotlinx Serialization, Caching).
- [x] Persistence Layer (DataStore Preferences & DiskCacheManager untuk akses offline).
- [x] Repositories (`QuranRepository`, `PrayerRepository` dengan kalkulasi hitung mundur waktu sholat otomatis).
- [x] Native Audio Engine Media3 ExoPlayer (`QuranAudioPlayer` untuk tilawah murottal per ayat dan penuh surat).
- [x] UI Design System OLED Murni (`#000000`, `#121212`, `#1E1E1E`, Emerald & Gold Accents).
- [x] Shimmer Skeleton Loaders (`SkeletonBox`, `shimmerEffect`).
- [x] Floating Dock Bar Navigation & Zero On-Screen Back Button (Navigasi kembali murni gesture native Android via `BackHandler`).
- [x] Layar Al-Qur'an (`QuranListScreen` dengan pencarian dan kartu terakhir dibaca).
- [x] Layar Baca Ayat (`SurahDetailScreen` dengan audio player bar dan bookmark).
- [x] Layar Jadwal Sholat (`PrayerTimesScreen` dengan hero countdown dan pencarian kota MyQuran v3).
- [x] Layar Tersimpan (`BookmarksScreen`).
- [x] Lolos Verifikasi Sintaksis & Keamanan Memori Kotlin (0 Hard Errors).
- [x] Setup Release Keystore & 4 GitHub Secrets (`KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`).
- [x] Kompilasi CI GitHub Actions Sukses & Terbit Release APK Signed v1.0.0.

## Catatan Bug Terselesaikan & Workaround (Anti-Mengulang Masalah)
- *Catatan CI Run #1:*
  1. `Unresolved reference 'animateFloat'` pada `SkeletonComponents.kt`: wajib menyertakan import eksplisit `androidx.compose.animation.core.animateFloat`.
  2. `items(4)` dipanggil di dalam non-lazy layout `Column` pada `SurahDetailScreen.kt`: layout vertikal standar harus menggunakan `repeat(4) { ... }`, bukan extension `items` dari `LazyListScope`.
- *Catatan API MyQuran v3:* ID kota berupa hash 32-karakter (`58a2fc6ed39fd083f55d4182bf88826d`). Format jadwal dikembalikan dalam map tanggal `"YYYY-MM-DD"`.
- *Catatan Linter Kotlin:* Hindari nested implicit `it` di dalam lambda yang sudah memiliki parameter bernama; beri nama eksplisit (`current ->`) pada `_uiState.update`.
