package com.gromber05.peco.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de Hilt encargado de proporcionar el DataStore de preferencias.
 *
 * Define una instancia singleton de [DataStore]<[Preferences]> utilizada
 * para almacenar preferencias de la aplicación de forma segura y persistente,
 * como configuraciones de usuario o ajustes de la app.
 *
 * Se instala en [SingletonComponent], garantizando una única instancia
 * durante todo el ciclo de vida de la aplicación.
 */
@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    /**
     * Proporciona una instancia singleton de [DataStore] para preferencias.
     *
     * @param context Contexto de la aplicación necesario para crear el archivo
     * de almacenamiento de preferencias.
     */
    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("peco_prefs") }
        )
}

