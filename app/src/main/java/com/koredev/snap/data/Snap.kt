package com.koredev.snap.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    indices = [
        Index("id")
    ],
    tableName = "snap"
)
data class Snap(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0,
    val path: String?,
    val date: Long = System.currentTimeMillis()
): Serializable