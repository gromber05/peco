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

@Singleton
class LocationRepository @Inject constructor(private val fused: FusedLocationProviderClient) {


    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(): Pair<Double, Double>? {
        return suspendCancellableCoroutine { cont ->
            fused.lastLocation
                .addOnSuccessListener { loc ->
                    cont.resume(loc?.let { it.latitude to it.longitude })
                }
                .addOnFailureListener {
                    cont.resume(null)
                }
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Pair<Double, Double>? {
        val cts = CancellationTokenSource()

        return suspendCancellableCoroutine { cont ->
            fused.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                .addOnSuccessListener { loc ->
                    cont.resume(loc?.let { it.latitude to it.longitude })
                }
                .addOnFailureListener {
                    cont.resume(null)
                }

            cont.invokeOnCancellation { cts.cancel() }
        }
    }
}
