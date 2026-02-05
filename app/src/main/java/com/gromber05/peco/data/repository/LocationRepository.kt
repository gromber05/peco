package com.gromber05.peco.data.repository

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Repositorio encargado de gestionar la obtención de coordenadas geográficas del dispositivo.
 * Utiliza el [FusedLocationProviderClient] para abstraer la complejidad de los proveedores de ubicación (GPS, Red, etc.).
 *
 * @property fused Cliente de servicios de ubicación de Google Play Services.
 */
@Singleton
class LocationRepository @Inject constructor(private val fused: FusedLocationProviderClient) {

    /**
     * Obtiene la última ubicación conocida almacenada en el dispositivo.
     * Es una operación rápida que no garantiza una ubicación actual exacta, sino la última registrada
     * por cualquier aplicación en el sistema.
     *
     * @return Un [Pair] con (Latitud, Longitud) o null si no hay ubicación disponible o falló la solicitud.
     */
    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(): Pair<Double, Double>? {
        return suspendCancellableCoroutine { cont ->
            fused.lastLocation
                .addOnSuccessListener { loc ->
                    // Resume la corrutina con las coordenadas o null si la ubicación es nula
                    cont.resume(loc?.let { it.latitude to it.longitude })
                }
                .addOnFailureListener {
                    // En caso de error, devolvemos null de forma segura
                    cont.resume(null)
                }
        }
    }

    /**
     * Solicita activamente la ubicación actual del dispositivo con alta precisión.
     * A diferencia de [getLastKnownLocation], esta función despierta los sensores (GPS/Wi-Fi)
     * para obtener una lectura reciente.
     *
     * @return Un [Pair] con (Latitud, Longitud) o null si se cancela o falla.
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Pair<Double, Double>? {
        // Fuente de token para poder cancelar la petición si la corrutina se cancela
        val cts = CancellationTokenSource()

        return suspendCancellableCoroutine { cont ->
            fused.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                .addOnSuccessListener { loc ->
                    cont.resume(loc?.let { it.latitude to it.longitude })
                }
                .addOnFailureListener {
                    cont.resume(null)
                }

            // Si el scope de la corrutina se cancela, cancelamos también la petición de Google
            cont.invokeOnCancellation { cts.cancel() }
        }
    }
}