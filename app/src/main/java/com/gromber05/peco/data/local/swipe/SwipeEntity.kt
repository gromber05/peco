package com.gromber05.peco.data.local.swipe

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.gromber05.peco.model.SwipeAction

@Entity(
    tableName = "swipes",
    indices = [Index(value = ["animalId"], unique = true)]
)
data class SwipeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val animalId: Int,
    val action: SwipeAction,
    val createdAt: Long = System.currentTimeMillis()
)
