package com.alquran.pro.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class FallbackCity(
    val name: String,
    val lat: Double,
    val lon: Double
)

object LocationHelper {

    private val INDONESIA_MAJOR_CITIES = listOf(
        FallbackCity("Jakarta", -6.2088, 106.8456),
        FallbackCity("Surabaya", -7.2575, 112.7521),
        FallbackCity("Bandung", -6.9175, 107.6191),
        FallbackCity("Medan", 3.5952, 98.6722),
        FallbackCity("Semarang", -6.9932, 110.4203),
        FallbackCity("Makassar", -5.1477, 119.4327),
        FallbackCity("Palembang", -2.9909, 104.7565),
        FallbackCity("Yogyakarta", -7.7956, 110.3695),
        FallbackCity("Denpasar", -8.6705, 115.2126),
        FallbackCity("Banjarmasin", -3.3194, 114.5908),
        FallbackCity("Padang", -0.9471, 100.3543),
        FallbackCity("Bandar Lampung", -5.4500, 105.2667),
        FallbackCity("Samarinda", -0.5016, 117.1537),
        FallbackCity("Pekanbaru", 0.5071, 101.4478),
        FallbackCity("Mataram", -8.5833, 116.1167),
        FallbackCity("Kupang", -10.1772, 123.6070),
        FallbackCity("Pontianak", -0.0263, 109.3425),
        FallbackCity("Manado", 1.4748, 124.8421),
        FallbackCity("Ambon", -3.6547, 128.1906),
        FallbackCity("Jayapura", -2.5337, 140.7181),
        FallbackCity("Banda Aceh", 5.5483, 95.3238),
        FallbackCity("Bengkulu", -3.7928, 102.2608),
        FallbackCity("Jambi", -1.6101, 103.6131),
        FallbackCity("Pangkal Pinang", -2.1333, 106.1167),
        FallbackCity("Tanjung Pinang", 0.9167, 104.4500),
        FallbackCity("Serang", -6.1104, 106.1640),
        FallbackCity("Gorontalo", 0.5435, 123.0568),
        FallbackCity("Palu", -0.8917, 119.8707),
        FallbackCity("Kendari", -3.9985, 122.5126),
        FallbackCity("Mamuju", -2.6748, 118.8885),
        FallbackCity("Ternate", 0.7918, 127.3831),
        FallbackCity("Manokwari", -0.8615, 134.0620),
        FallbackCity("Sorong", -0.8762, 131.2558)
    )

    fun hasLocationPermission(context: Context): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(context: Context): Location? = withContext(Dispatchers.IO) {
        if (!hasLocationPermission(context)) return@withContext null

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return@withContext null

        // 1. Cek last known location dari beberapa provider (GPS, Network, Passive)
        val providers = listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER,
            LocationManager.PASSIVE_PROVIDER
        )

        var bestLocation: Location? = null
        for (provider in providers) {
            try {
                if (locationManager.isProviderEnabled(provider)) {
                    val loc = locationManager.getLastKnownLocation(provider)
                    if (loc != null) {
                        if (bestLocation == null || loc.accuracy < bestLocation.accuracy) {
                            bestLocation = loc
                        }
                    }
                }
            } catch (_: SecurityException) {
            } catch (_: Exception) {
            }
        }

        if (bestLocation != null) {
            return@withContext bestLocation
        }

        // 2. Jika belum ada last known location, minta single update dengan timeout 5 detik
        withTimeoutOrNull(5000L) {
            suspendCancellableCoroutine<Location?> { cont ->
                val listener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        try {
                            locationManager.removeUpdates(this)
                        } catch (_: Exception) {}
                        if (cont.isActive) {
                            cont.resume(location)
                        }
                    }
                    @Deprecated("Deprecated in Java")
                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {}
                }

                var requested = false
                for (provider in listOf(LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER)) {
                    try {
                        if (locationManager.isProviderEnabled(provider)) {
                            locationManager.requestLocationUpdates(
                                provider,
                                0L,
                                0f,
                                listener,
                                Looper.getMainLooper()
                            )
                            requested = true
                            break
                        }
                    } catch (_: SecurityException) {
                    } catch (_: Exception) {
                    }
                }

                if (!requested) {
                    if (cont.isActive) cont.resume(null)
                }

                cont.invokeOnCancellation {
                    try {
                        locationManager.removeUpdates(listener)
                    } catch (_: Exception) {}
                }
            }
        }
    }

    suspend fun resolveCitySearchCandidates(context: Context, location: Location): List<String> =
        withContext(Dispatchers.IO) {
            val candidates = linkedSetOf<String>()

            try {
                if (Geocoder.isPresent()) {
                    val geocoder = Geocoder(context, Locale("id", "ID"))
                    val addresses: List<Address> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        suspendCancellableCoroutine { continuation ->
                            geocoder.getFromLocation(
                                location.latitude,
                                location.longitude,
                                3,
                                object : Geocoder.GeocodeListener {
                                    override fun onGeocode(addresses: MutableList<Address>) {
                                        continuation.resume(addresses)
                                    }
                                    override fun onError(errorMessage: String?) {
                                        continuation.resume(emptyList())
                                    }
                                }
                            )
                        }
                    } else {
                        @Suppress("DEPRECATION")
                        geocoder.getFromLocation(location.latitude, location.longitude, 3) ?: emptyList()
                    }

                    for (address in addresses) {
                        address.subAdminArea?.let { candidates.addAll(cleanLocationKeywords(it)) }
                        address.locality?.let { candidates.addAll(cleanLocationKeywords(it)) }
                        address.adminArea?.let { candidates.addAll(cleanLocationKeywords(it)) }
                    }
                }
            } catch (_: Exception) {
            }

            // Fallback terdekat berdasarkan koordinat haversine jika geocoder kosong
            val nearestFallback = findNearestMajorCity(location.latitude, location.longitude)
            candidates.add(nearestFallback.name.lowercase())

            candidates.filter { it.isNotBlank() }
        }

    fun cleanLocationKeywords(raw: String): List<String> {
        val cleaned = raw
            .replace(Regex("(?i)\\b(kabupaten|kab\\.?|kota|daerah khusus ibukota|daerah istimewa|dki|diy|provinsi|prov\\.?)\\b"), "")
            .replace(Regex("[,.-]"), " ")
            .trim()
            .replace(Regex("\\s+"), " ")

        if (cleaned.isBlank()) return emptyList()

        val list = mutableListOf<String>()
        list.add(cleaned.lowercase())

        val words = cleaned.split(" ")
        if (words.size > 1 && words[0].length >= 3) {
            list.add(words[0].lowercase())
        }
        return list
    }

    fun findNearestMajorCity(lat: Double, lon: Double): FallbackCity {
        var minDistance = Double.MAX_VALUE
        var nearest = INDONESIA_MAJOR_CITIES.first()

        for (city in INDONESIA_MAJOR_CITIES) {
            val dist = haversineDistanceKm(lat, lon, city.lat, city.lon)
            if (dist < minDistance) {
                minDistance = dist
                nearest = city
            }
        }
        return nearest
    }

    private fun haversineDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Radius bumi dalam km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
