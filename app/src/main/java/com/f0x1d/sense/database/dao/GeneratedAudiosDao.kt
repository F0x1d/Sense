package com.f0x1d.sense.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.f0x1d.sense.database.entity.GeneratedAudio
import kotlinx.coroutines.flow.Flow

@Dao
interface GeneratedAudiosDao {

    @Query("SELECT * FROM GeneratedAudio")
    fun getAll(): Flow<List<GeneratedAudio>>

    @Insert
    suspend fun insert(audio: GeneratedAudio)

    @Delete
    suspend fun delete(audio: GeneratedAudio)
}