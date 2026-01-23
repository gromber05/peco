package com.gromber05.peco.data.local.message

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY createdAt ASC")
    fun observeMessages(conversationId: Int): Flow<List<MessageEntity>>

    @Insert
    suspend fun insert(message: MessageEntity)
}
