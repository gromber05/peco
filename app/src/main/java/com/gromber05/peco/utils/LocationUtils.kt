package com.gromber05.peco.utils

import android.location.Location
import android.location.Geocoder
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

object LocationUtils {
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

    @Composable
    fun rememberCityFromLatLng(
        latitude: Double,
        longitude: Double
    ): String {
        val context = LocalContext.current
        var city by remember(latitude, longitude) { mutableStateOf("Cargando…") }

        LaunchedEffect(latitude, longitude) {
            city = withContext(Dispatchers.IO) {
                try {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                    addresses?.firstOrNull()?.locality
                        ?: addresses?.firstOrNull()?.subAdminArea
                        ?: "Ubicación desconocida"
                } catch (_: Exception) {
                    "Ubicación desconocida"
                }
            }
        }

        return city
    }

}