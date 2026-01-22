package com.gromber05.peco.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gromber05.peco.data.local.swipe.SwipeDao
import com.gromber05.peco.data.local.swipe.SwipeEntity
import com.gromber05.peco.data.local.animal.AnimalDao
import com.gromber05.peco.data.local.animal.AnimalEntity
import com.gromber05.peco.data.local.user.UserDao
import com.gromber05.peco.data.local.user.UserEntity
import com.gromber05.peco.utils.converters.AdoptionStateConverter
import com.gromber05.peco.utils.converters.SwipeConverters
import com.gromber05.peco.utils.converters.UserRoleConverter

@Database(
    entities = [UserEntity::class, AnimalEntity::class, SwipeEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(AdoptionStateConverter::class, SwipeConverters::class, UserRoleConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun animalDao(): AnimalDao
    abstract fun swipeDao(): SwipeDao
}