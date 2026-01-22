package com.gromber05.peco.data.di

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseApp(@ApplicationContext context: Context) =
        FirebaseApp.initializeApp(context) ?: FirebaseApp.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(app: FirebaseApp): FirebaseFirestore =
        FirebaseFirestore.getInstance()
}
