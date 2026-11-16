# Project State & Session Memory

## Status Saat Ini
- **Fase Aktif:** Selesai (Release v1.2.0 Sukses Diterbitkan & Diunduh ke HP)
- **Versi Terakhir:** v1.2.0 (versionCode: 3)
- **Status CI Terakhir:** Hijau (Build signed release APK & GitHub Release Publik Berhasil)
- **Tautan Rilis v1.2.0:** https://github.com/arbdevai/ALQURAN-PRO/releases/tag/v1.2.0

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
- [x] Set Ikon Vektor Lucide Murni (`ic_lucide_home`, `ic_lucide_book_open`, `ic_lucide_clock`, `ic_lucide_bookmark`, `ic_lucide_compass`, `ic_lucide_sparkles`, `ic_lucide_search`, `ic_lucide_map_pin`, `ic_lucide_history`, `ic_lucide_volume_2`, `ic_lucide_info`).
- [x] Dashboard Beranda Bento Grid Compact Tiles (`HomeScreen` & `BentoMenuCard` hemat ruang vertikal layar ~68dp).
- [x] Penghapusan Menu Redundan (Cari Surah dan Arah Kiblat dihapus sesuai permintaan pengguna).
- [x] Dialog Info & Sumber Resmi (`AboutAppDialog`: Developer Sahal Arbani, Sumber EQuran v2 Kemenag RI, Sumber MyQuran v3 Bimas Islam).
- [x] Modul In-App Updater Mandiri (`InAppUpdateManager` & `UpdateViewModel`: OkHttp streaming `.part`, verifikasi 4 lapis `isApkValid`, deteksi ABI CPU `Build.SUPPORTED_ABIS`, instalasi via FileProvider).
- [x] Floating Dock Bar 4 Tab Modern (Beranda, Al-Qur'an, Jadwal, Tersimpan).
- [x] Navigasi Gestur Murni Tanpa Tombol Kembali di Layar (Mengandalkan `BackHandler` dan native system back gesture).
- [x] Layar Al-Qur'an, Baca Ayat, Jadwal Sholat, dan Bookmark.
- [x] Lolos Verifikasi Sintaksis & Keamanan Memori Kotlin (0 Hard Errors).
- [x] Unduhan APK Rilis ARM64 Langsung ke `/sdcard/Download/AlQuranPro-v1.2.0-arm64.apk`.

## Catatan Bug Terselesaikan & Workaround (Anti-Mengulang Masalah)
- *Catatan CI Run #1:*
  1. `Unresolved reference 'animateFloat'` pada `SkeletonComponents.kt`: wajib menyertakan import eksplisit `androidx.compose.animation.core.animateFloat`.
  2. `items(4)` dipanggil di dalam non-lazy layout `Column` pada `SurahDetailScreen.kt`: layout vertikal standar harus menggunakan `repeat(4) { ... }`, bukan extension `items` dari `LazyListScope`.
- *Catatan API MyQuran v3:* ID kota berupa hash 32-karakter (`58a2fc6ed39fd083f55d4182bf88826d`). Format jadwal dikembalikan dalam map tanggal `"YYYY-MM-DD"`.
- *Catatan Linter Kotlin:* Hindari nested implicit `it` di dalam lambda yang sudah memiliki parameter bernama; beri nama eksplisit (`current ->`) pada `_uiState.update`.
