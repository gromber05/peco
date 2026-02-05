package com.gromber05.peco.data.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de Hilt encargado de proporcionar dependencias relacionadas con la localización.
 *
 * Proporciona una instancia singleton de [FusedLocationProviderClient], utilizada
 * para obtener la ubicación del dispositivo de forma eficiente y optimizada.
 *
 * El uso de [ApplicationContext] garantiza que el contexto inyectado sea seguro
 * y válido durante todo el ciclo de vida de la aplicación.
 */
@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    /**
     * Proporciona una instancia singleton de [FusedLocationProviderClient].
     *
     * @param context Contexto de la aplicación necesario para inicializar el cliente
     * de localización.
     */
    @Provides
    @Singleton
    fun provideFusedLocationClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
}
