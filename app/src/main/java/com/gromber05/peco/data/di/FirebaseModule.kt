package com.gromber05.peco.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de Hilt encargado de proporcionar las dependencias de Firebase.
 *
 * Este módulo define los proveedores de instancias únicas (Singleton) de:
 * - [FirebaseAuth] para la autenticación de usuarios.
 * - [FirebaseFirestore] como base de datos en la nube.
 * - [FirebaseStorage] para el almacenamiento de archivos.
 *
 * Se instala en [SingletonComponent], por lo que las dependencias viven
 * durante todo el ciclo de vida de la aplicación.
 *
 * Gracias a este módulo, las dependencias de Firebase pueden inyectarse
 * de forma segura y desacoplada en ViewModels, Repositories y DataSources.
 */
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    /**
     * Proporciona una instancia singleton de [FirebaseAuth].
     */
    @Provides
    @Singleton
    fun provideAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Proporciona una instancia singleton de [FirebaseFirestore].
     */
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * Proporciona una instancia singleton de [FirebaseStorage].
     */
    @Provides
    @Singleton
    fun provideStorage(): FirebaseStorage = FirebaseStorage.getInstance()
}
