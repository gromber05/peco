package com.gromber05.peco.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gromber05.peco.data.local.animal.AnimalDao
import com.gromber05.peco.data.local.animal.AnimalEntity
import com.gromber05.peco.data.local.user.UserDao
import com.gromber05.peco.data.local.user.UserEntity
import com.gromber05.peco.utils.converters.AdoptionStateConverter

@Database(
    entities = [UserEntity::class, AnimalEntity::class],
    version = 2
)
@TypeConverters(AdoptionStateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun animalDao(): AnimalDao
}