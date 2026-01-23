package com.gromber05.peco.data.local.conversation

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "conversations",
    indices = [Index(value = ["userAId", "userBId"], unique = true)]
)
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userAId: Int,
    val userBId: Int,
    val createdAt: Long = System.currentTimeMillis()
)
