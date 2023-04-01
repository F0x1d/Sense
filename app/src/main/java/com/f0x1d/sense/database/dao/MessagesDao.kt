package com.f0x1d.sense.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.f0x1d.sense.database.entity.ChatMessage

@Dao
interface MessagesDao {

    @Query("SELECT COUNT(*) FROM ChatMessage WHERE content is null")
    suspend fun countEmptyMessages(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg message: ChatMessage): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(messages: List<ChatMessage>)

    @Query("DELETE FROM ChatMessage WHERE content is null")
    suspend fun deleteEmptyMessages()
}