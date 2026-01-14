package com.gromber05.peco.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gromber05.peco.data.local.user.UserDao
import com.gromber05.peco.data.local.user.UserEntity

@Database(entities = [UserEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}