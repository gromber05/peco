package com.gromber05.peco.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.gromber05.peco.data.local.AppDatabase
import com.gromber05.peco.data.local.animal.AnimalDao
import com.gromber05.peco.data.local.swipe.SwipeDao
import com.gromber05.peco.data.local.swipe.SwipeEntity
import com.gromber05.peco.data.local.user.UserDao
import com.gromber05.peco.data.remote.chat.ChatFirebaseDataSource
import com.gromber05.peco.data.repository.ChatRepository
import com.gromber05.peco.data.repository.ChatRepositoryImpl
import com.gromber05.peco.data.session.AppPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "peco_database"
        )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)

                    db.execSQL(
                        """
                        INSERT INTO users (username, email, password, photo, isAdmin)
                        VALUES ('Super Admin', 'admin@admin.es', 'fernandoapruebame', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSp-7WmGrclnPy4Y7vQMYkdkORBUeQcmyVigw&s', 1)
                    """.trimIndent()
                    )

                    db.execSQL(
                        """
                        INSERT INTO users (username, email, password, photo, isAdmin)
                        VALUES ('Usuario', 'usuario@usuario.es', 'usuario', '', 0)
                    """.trimIndent()
                    )

                    db.execSQL(
                        """
                        INSERT INTO animals (id, name, species, photo, dob, latitude, longitude, adoptionState)
                        VALUES (1, 'Luna', 'Perro', 'https://eq2imhfmrcc.exactdn.com/wp-content/uploads/2016/08/golden-retriever.jpg?strip=all', '2022-03-01', 36.5271, -6.2886, 'AVAILABLE')
                    """.trimIndent()
                    )

                    db.execSQL(
                        """
                        INSERT INTO animals (id, name, species, photo, dob, latitude, longitude, adoptionState)
                        VALUES (2, 'Milo', 'Gato', 'https://eq2imhfmrcc.exactdn.com/wp-content/uploads/2016/08/golden-retriever.jpg?strip=all', '2021-07-12', 36.6864, -6.1372, 'AVAILABLE')
                    """.trimIndent()
                    )

                    db.execSQL(
                        """
                        INSERT INTO animals (id, name, species, photo, dob, latitude, longitude, adoptionState)
                        VALUES (3, 'Nala', 'Perro', 'https://eq2imhfmrcc.exactdn.com/wp-content/uploads/2016/08/golden-retriever.jpg?strip=all', '2020-11-05', 36.4657, -6.1967, 'AVAILABLE')
                    """.trimIndent()
                    )

                    db.execSQL(
                        """
                        INSERT INTO animals (id, name, species, photo, dob, latitude, longitude, adoptionState)
                        VALUES (4, 'Simba', 'Gato', 'https://eq2imhfmrcc.exactdn.com/wp-content/uploads/2016/08/golden-retriever.jpg?strip=all', '2019-09-20', 36.5297, -6.2923, 'AVAILABLE')
                    """.trimIndent()
                    )
                }
            })
            .fallbackToDestructiveMigration(false)
            .build()


    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideAnimalDao(database: AppDatabase): AnimalDao {
        return database.animalDao()
    }

    @Provides
    fun provideSwipeDao(database: AppDatabase): SwipeDao {
        return database.swipeDao()
    }

    @Provides
    @Singleton
    fun provideAppPreferences(@ApplicationContext context: Context): AppPreferences =
        AppPreferences(context)

    @Provides @Singleton
    fun provideChatDataSource(db: FirebaseFirestore): ChatFirebaseDataSource =
        ChatFirebaseDataSource(db)

    @Provides @Singleton
    fun provideChatRepository(ds: ChatFirebaseDataSource): ChatRepository =
        ChatRepositoryImpl(ds)
}