package com.gromber05.peco.data.repository

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationRepository(private val context: Context) {

    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(): Pair<Double, Double>? {
        val client = LocationServices.getFusedLocationProviderClient(context)

        return suspendCancellableCoroutine { cont ->
            client.lastLocation
                .addOnSuccessListener { loc ->
                    cont.resume(loc?.latitude?.let { lat -> lat to loc.longitude })
                }
                .addOnFailureListener {
                    cont.resume(null)
                }
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Pair<Double, Double>? {
        val client = LocationServices.getFusedLocationProviderClient(context)
        val cts = CancellationTokenSource()

        return suspendCancellableCoroutine { cont ->
            client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                .addOnSuccessListener { loc ->
                    if (loc != null) cont.resume(loc.latitude to loc.longitude)
                    else cont.resume(null)
                }
                .addOnFailureListener { cont.resume(null) }

            cont.invokeOnCancellation { cts.cancel() }
        }
    }
}
