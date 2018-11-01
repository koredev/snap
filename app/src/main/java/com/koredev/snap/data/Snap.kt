package com.koredev.snap.data

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
    @PrimaryKey
    val id: Int,
    val snapId: String,
    val path: String?,
    val date: Long
): Serializable