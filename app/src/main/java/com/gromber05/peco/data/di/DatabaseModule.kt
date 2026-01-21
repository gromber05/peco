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
import com.gromber05.peco.data.repository.LocationRepository
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
                        VALUES ('Admin', 'admin@admin.es', 'fernandoapruebame', 'https://i.pinimg.com/originals/3f/72/2e/3f722e7be5a952584063a35048820e89.png', 1)
                    """.trimIndent()
                    )

                    db.execSQL(
                        """
                        INSERT INTO users (username, email, password, photo, isAdmin)
                        VALUES ('Usuario', 'usuario@usuario.es', 'usuario', 'https://www.pokemon.com/static-assets/content-assets/cms2/img/pokedex/full/004.png', 0)
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
                        VALUES (2, 'Milo', 'Gato', 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/4d/Cat_November_2010-1a.jpg/250px-Cat_November_2010-1a.jpg', '2021-07-12', 36.6864, -6.1372, 'AVAILABLE')
                    """.trimIndent()
                    )

                    db.execSQL(
                        """
                        INSERT INTO animals (id, name, species, photo, dob, latitude, longitude, adoptionState)
                        VALUES (3, 'Nala', 'Perro', 'https://cdn.sanity.io/images/5vm5yn1d/pro/5cb1f9400891d9da5a4926d7814bd1b89127ecba-1300x867.jpg?fm=webp&q=80', '2020-11-05', 36.4657, -6.1967, 'AVAILABLE')
                    """.trimIndent()
                    )

                    db.execSQL(
                        """
                        INSERT INTO animals (id, name, species, photo, dob, latitude, longitude, adoptionState)
                        VALUES (4, 'Simba', 'Gato', 'https://i.pinimg.com/736x/9a/bf/f2/9abff24eab348a5394c6b90225768242.jpg', '2019-09-20', 36.5297, -6.2923, 'AVAILABLE')
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
    fun provideAppPreferences(@ApplicationContext context: Context): AppPreferences {
        return AppPreferences(context)
    }

    @Provides
    @Singleton
    fun provideLocationRepository(@ApplicationContext context: Context): LocationRepository {
        return LocationRepository(context)
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