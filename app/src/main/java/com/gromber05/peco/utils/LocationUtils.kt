package com.gromber05.peco.utils

import android.location.Location
import android.location.Geocoder
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

/**
 * Utilidades relacionadas con localización y geocodificación.
 *
 * Este objeto agrupa funciones auxiliares para:
 * - Calcular distancias geográficas entre dos puntos.
 * - Obtener información legible de ubicación (ciudad) a partir de coordenadas.
 */
object LocationUtils {

    /**
     * Calcula la distancia en metros entre dos coordenadas geográficas.
     *
     * Utiliza el método nativo [Location.distanceBetween], que tiene en cuenta
     * la curvatura de la Tierra.
     *
     * @param lat1 Latitud del primer punto.
     * @param lon1 Longitud del primer punto.
     * @param lat2 Latitud del segundo punto.
     * @param lon2 Longitud del segundo punto.
     * @return Distancia en metros entre ambos puntos.
     */
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

    /**
     * Obtiene y recuerda el nombre de la ciudad asociada a unas coordenadas.
     *
     * Esta función es *Composable* y está pensada para usarse directamente
     * desde la UI con Jetpack Compose.
     *
     * Comportamiento:
     * - Devuelve inicialmente el texto `"Cargando…"`.
     * - Realiza la geocodificación en un hilo IO.
     * - Intenta obtener primero la localidad (`locality`) y, si no existe,
     *   el área administrativa secundaria (`subAdminArea`).
     * - Si ocurre cualquier error o no hay resultados, devuelve
     *   `"Ubicación desconocida"`.
     *
     * El resultado se memoriza y solo se recalcula cuando cambian
     * `latitude` o `longitude`.
     *
     * @param latitude Latitud del punto.
     * @param longitude Longitud del punto.
     * @return Nombre de la ciudad o una cadena descriptiva del estado.
     */
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
