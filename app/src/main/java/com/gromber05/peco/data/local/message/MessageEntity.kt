package com.gromber05.peco.data.local.message

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "messages",
    indices = [Index(value = ["conversationId", "createdAt"])]
)
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val conversationId: Int,
    val senderId: Int,
    val text: String,
    val createdAt: Long = System.currentTimeMillis()
)
