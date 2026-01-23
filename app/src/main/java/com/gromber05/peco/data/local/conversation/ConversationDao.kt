package com.gromber05.peco.data.local.conversation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ConversationDao {

    @Query("""
        SELECT * FROM conversations
        WHERE (userAId = :me AND userBId = :other)
           OR (userAId = :other AND userBId = :me)
        LIMIT 1
    """)
    suspend fun findBetweenUsers(me: Int, other: Int): ConversationEntity?

    @Insert
    suspend fun insert(conversation: ConversationEntity): Long
}
