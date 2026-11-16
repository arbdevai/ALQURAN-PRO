# Project State & Session Memory

## Status Saat Ini
- **Fase Aktif:** Release v1.3.0
- **Versi Terakhir:** v1.3.0 (versionCode: 4)
- **Status CI Terakhir:** Mempersiapkan Build & Release GitHub Actions

## Fitur yang Selesai Tuntas (Vertical Slices)
- [x] Inisiasi Proyek Android Compose (Single Activity, Gradle Catalog, Android 15/16 Ready).
- [x] Konfigurasi CI/CD GitHub Actions (`build-apk.yml`) dengan Auto-Release & Signing.
- [x] Dokumen PRD dan Kontrak Desain (True OLED Dark, Zero On-screen Back Button, Zero AI Slop).
- [x] DTO & Model Data EQuran v2 (`SuratSummaryDto`, `SuratDetailDto`, `AyatDto`, `TafsirDto`).
- [x] Perbaikan Deserialisasi Al-Fatihah & An-Nas (`SuratNavSerializer` menangani respons `false` boolean dari API EQuran v2).
- [x] DTO & Model Data MyQuran v3 (`KotaDto`, `JadwalResponse`, `JadwalHariIniDto`).
- [x] Unified Prayer Card (`UnifiedPrayerScheduleCard`: Menggabungkan lokasi, waktu sholat berikutnya, hitung mundur, dan 8 slot jadwal ke dalam 1 kartu tunggal efisien).
- [x] Deteksi Lokasi GPS Otomatis (`LocationHelper` & `PrayerRepository.findCityByLocation`): Mendukung permission Compose native, `LocationManager`, `Geocoder`, pembersihan keyword multi-kandidat, dan fallback haversine kota terdekat.
- [x] Ikon Vektor Lucide Crosshair (`ic_lucide_crosshair.xml`) untuk tombol deteksi lokasi.
- [x] Network Layer (Retrofit, OkHttp, Kotlinx Serialization, Caching).
- [x] Persistence Layer (DataStore Preferences & DiskCacheManager untuk akses offline).
- [x] Repositories (`QuranRepository`, `PrayerRepository` dengan kalkulasi hitung mundur waktu sholat otomatis).
- [x] Native Audio Engine Media3 ExoPlayer (`QuranAudioPlayer` untuk tilawah murottal per ayat dan penuh surat).
- [x] UI Design System OLED Murni (`#000000`, `#121212`, `#1E1E1E`, Emerald & Gold Accents).
- [x] Shimmer Skeleton Loaders (`SkeletonBox`, `shimmerEffect`).
- [x] Set Ikon Vektor Lucide Murni.
- [x] Dashboard Beranda Bento Grid Compact Tiles.
- [x] Dialog Info & Sumber Resmi (`AboutAppDialog`: Developer Sahal Arbani, Sumber EQuran v2 Kemenag RI, Sumber MyQuran v3 Bimas Islam).
- [x] Modul In-App Updater Mandiri (`InAppUpdateManager` & `UpdateViewModel`: OkHttp streaming `.part`, verifikasi 4 lapis `isApkValid`, deteksi ABI CPU `Build.SUPPORTED_ABIS`, instalasi via FileProvider).
- [x] Floating Dock Bar 4 Tab Modern (Beranda, Al-Qur'an, Jadwal, Tersimpan).
- [x] Navigasi Gestur Murni Tanpa Tombol Kembali di Layar (Mengandalkan `BackHandler` dan native system back gesture).
- [x] Lolos Verifikasi Sintaksis & Keamanan Memori Kotlin (0 Hard Errors).

## Catatan Bug Terselesaikan & Workaround (Anti-Mengulang Masalah)
1. **Crash Deserialisasi Surat Al-Fatihah (Surat 1):**
   - *Penyebab:* Endpoint `https://equran.id/api/v2/surat/1` mengembalikan `"suratSebelumnya": false` (nilai primitif boolean), bukan `null` ataupun objek JSON. Demikian pula surat 114 (An-Nas) terhadap `"suratSelanjutnya"`.
   - *Solusi:* Implementasi `SuratNavSerializer` custom serializer yang memvalidasi `element is JsonObject` dan mengembalikan `null` dengan aman bila bertemu boolean `false`.
2. **Jadwal Sholat Terpisah-pisah:**
   - *Penyebab:* Hero card dan 8 slot sholat sebelumnya dibuat dalam kartu-kartu terpisah berjarak, memakan ruang layar vertikal.
   - *Solusi:* Digabung menjadi satu kartu terpadu `UnifiedPrayerScheduleCard` dengan header lokasi, tombol GPS, sub-hero countdown, pembatas tipis, dan list 8 slot waktu sholat ber-highlight rapi di dalam kartu yang sama.
3. **Pencarian Lokasi Jadwal Sholat:**
   - *Penyebab:* MyQuran v3 hanya memiliki kota/kabupaten resmi tingkat II (517 entitas). Koordinat GPS pengguna sering menghasilkan nama kecamatan atau distrik yang tidak ada di daftar.
   - *Solusi:* Dibuat `resolveCitySearchCandidates` untuk mengekstrak kabupaten, kota, atau provinsi, membersihkan kata awalan ("Kabupaten", "Kota", dll.), dan mencari bertahap, serta fallback jarak haversine ke ibukota/kota besar terdekat bila Geocoder kosong.
