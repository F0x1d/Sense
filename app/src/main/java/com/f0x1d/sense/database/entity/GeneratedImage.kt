package com.f0x1d.sense.database.entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity
data class GeneratedImage(
    @ColumnInfo(name = "prompt") val prompt: String? = null,
    @ColumnInfo(name = "url") val url: String? = null,
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Long = 0
)