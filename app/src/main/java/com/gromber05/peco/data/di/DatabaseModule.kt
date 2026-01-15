package com.gromber05.peco.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gromber05.peco.data.local.AppDatabase
import com.gromber05.peco.data.local.animal.AnimalDao
import com.gromber05.peco.data.local.user.UserDao
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
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase = Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "peco_database"
            ).addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)

                    db.execSQL(
                        """
                            INSERT INTO users (username, email, password, photo, isAdmin)
                            VALUES ('Super Admin', 'admin', 'fernandoapruebame', null, 1)
                            """
                    )
                }
            }).addMigrations().build()

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideAnimalDao(database: AppDatabase): AnimalDao {
        return database.animalDao()
    }
}