# Product Requirement Document (PRD)

## 1. Identitas Aplikasi
- **Nama Aplikasi:** AlQuranPro
- **Package Name:** com.alquran.pro
- **Min SDK / Target SDK:** 26 / 36 (Android 15 & 16 Ready)
- **Arsitektur:** Clean Architecture (Data -> Domain -> UI), Jetpack Compose Material3, Single-Activity, Unidirectional Data Flow (StateFlow + MVI/MVVM).
- **Filosofi UI/UX:**
  - **Fresh, Non-Generic Native Android:** Bebas tampilan template web micro-SaaS murahan. Menggunakan palet **True OLED Dark (#000000 murni)** dengan kontras tinggi, permukaan solid `#121212`, aksen Emerald `#10B981` dan Gold `#F59E0B`.
  - **Zero On-Screen Back Button Clutter:** Sesuai permintaan pengguna ("jangan ada tombol kembali di layar hp ini aplikasi native bukan web"), seluruh navigasi sub-halaman menggunakan native Android gesture navigation dan `BackHandler`. Layar utama mengadopsi Modern Floating Dock / Bottom Bar dengan transisi `AnimatedContent` yang mulus.
  - **Anti-AI Slop & Zero Emoji:** Bebas pulsing dot, bebas glowing neon, bebas kartu transparan sirup, dan bebas emoji di seluruh UI. Menggunakan ikon vektor resmi Android Material Symbols / Icons.
  - **Zero Dummy Data & Shimmer Skeleton:** Semua status pemuatan jaringan menampilkan skeleton shimmer responsif.

## 2. Integrasi Layanan & API Eksternal
1. **EQuran.id API v2 (Al-Qur'an):**
   - Base URL: `https://equran.id/api/v2`
   - Daftar 114 Surat: `GET /surat`
   - Detail Surat & Ayat (Teks Arab, Latin, Terjemahan ID, Audio Ayat): `GET /surat/{nomor}`
   - Tafsir Surat: `GET /tafsir/{nomor}`
2. **MyQuran API v3 (Jadwal Sholat & Waktu Imsakiyah):**
   - Base URL: `https://api.myquran.com/v3/sholat`
   - Pencarian Kabupaten/Kota: `GET /kabkota/cari/{keyword}`
   - Jadwal Hari Ini: `GET /jadwal/{id_kota_hash}/today?tz=Asia/Jakarta`
   - Waktu Sholat: Imsak, Subuh, Terbit, Dhuha, Dzuhur, Ashar, Maghrib, Isya dengan hitung mundur sholat berikutnya (*Next Prayer Countdown*).

## 3. Kebutuhan Teknis & Persistence
- **Audio Engine:** Android MediaPlayer / ExoPlayer Media3 untuk streaming audio tilawah ayat demi ayat dan surat penuh dengan status play/pause/stop yang persisten.
- **Persistence Layer:**
  - `DataStore Preferences`: Menyimpan lokasi kota aktif, pengaturan ukuran font mushaf (Kecil, Standar, Besar), bookmark ayat terakhir dibaca (*Last Read*), dan daftar bookmark favorit.
  - `Offline Disk Cache`: Penyimpanan lokal respons JSON surat dan jadwal sholat di penyimpanan internal aplikasi untuk akses instan tanpa kuota saat offline.
- **In-App Updater:** Modul pembaruan mandiri terintegrasi via GitHub Releases dengan isolasi file `.part` dan validasi checksum APK.

## 4. Daftar Fitur Utama (Vertical Slices)
- [ ] **Fitur 1: Fondasi Arsitektur, Design System OLED & Scaffolding**
  - Tema True OLED Dark, Typography Arab (Amiri / Scheherazade fallback native Arabic Naskh), Shimmer Effect, Scaffold dengan Insets + IME Padding.
- [ ] **Fitur 2: Modul Al-Qur'an (EQuran v2)**
  - Daftar 114 Surat lengkap (nama Arab, transliterasi Latin, arti, jumlah ayat, tempat turun Makkiyah/Madaniyah).
  - Pencarian surat instan (berdasarkan nama Latin, nomor, arti).
  - Layar Baca Surat (Teks Arab kontras tinggi, nomor ayat kaligrafi, teks Latin, terjemahan Indonesia).
  - Fitur "Terakhir Dibaca" (*Last Read Tracker*) dan Bookmark Ayat.
  - Audio Murottal Player per ayat dan per surat penuh.
- [ ] **Fitur 3: Modul Jadwal Sholat (MyQuran v3)**
  - Kartu Waktu Sholat Real-time dengan countdown ke waktu sholat berikutnya.
  - Indikator sholat aktif saat ini (Imsak, Subuh, Terbit, Dhuha, Dzuhur, Ashar, Maghrib, Isya).
  - Fitur ganti kota/kabupaten dengan live search ke endpoint MyQuran v3.
  - Cache jadwal harian untuk keandalan offline.
- [x] **Fitur 4: Navigasi Gestur & Hub Layar**
  - Modern Floating Dock Bar untuk navigasi cepat (Al-Qur'an, Jadwal Sholat, Bookmark & Pengaturan).
  - Penanganan `BackHandler` mulus tanpa tombol kembali statis di antarmuka layar.
- [x] **Fitur 5: Dashboard Beranda Bento Grid & Set Ikon Lucide Murni**
  - Halaman Beranda (*HomeScreen*) sebagai central hub modular.
  - Kartu Hero Sholat Terdekat (MyQuran v3) dengan hitung mundur waktu sholat dan ganti kota instan.
  - Kartu Resume Cepat (*Last Read Quick Access*).
  - Kisi Menu Kotak-Kotak Kompak (*Compact Bento Tiles 2 Kolom, ~68dp*) untuk efisiensi ruang layar.
  - Set Ikon Vektor Lucide murni berbasis Android XML Vector Drawable (`res/drawable/ic_lucide_*`).
  - Dialog Info Aplikasi & Sumber Resmi (Profil Developer: Sahal Arbani, EQuran v2 Kemenag RI, MyQuran v3).
- [x] **Fitur 6: In-App Updater Mandiri (GitHub Releases Integration)**
  - Modul pembaruan mandiri otomatis tanpa Google Play Store ([InAppUpdateManager.kt](file:///home/coder/projects/ALQURAN-PRO/app/src/main/java/com/alquran/pro/updater/InAppUpdateManager.kt)).
  - Deteksi arsitektur CPU otomatis (`Build.SUPPORTED_ABIS`) khusus ARM64 (`arm64-v8a`) dan ARM32 (`armeabi-v7a`).
  - Pengunduhan streaming OkHttp terisolasi ke file sementara `.part` dengan pemantauan progres persentase.
  - Verifikasi integritas APK 4 lapis (`isApkValid`: ukuran fisik > 10MB, DEX & Signature parsing, strict package name match, SemVer comparison).
  - Pemicu instalasi via Android `FileProvider` dan pengecekan izin `ACTION_MANAGE_UNKNOWN_APP_SOURCES`.
