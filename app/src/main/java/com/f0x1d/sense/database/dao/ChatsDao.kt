package com.f0x1d.sense.database.dao

import androidx.room.*
import com.f0x1d.sense.database.entity.Chat
import com.f0x1d.sense.database.entity.ChatWithMessages
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatsDao {

    @Transaction
    @Query("SELECT * FROM Chat")
    fun getAll(): Flow<List<ChatWithMessages>>

    @Transaction
    @Query("SELECT * FROM Chat WHERE id = :id")
    fun getById(id: Long): Flow<ChatWithMessages>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chat: Chat): Long

    @Delete
    suspend fun delete(chat: Chat)
}