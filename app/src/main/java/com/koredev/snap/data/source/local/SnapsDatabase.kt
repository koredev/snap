package com.koredev.snap.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.koredev.snap.data.Snap

@Database(entities = [Snap::class], version = 3)
abstract class SnapsDatabase : RoomDatabase() {
    abstract fun snapsDao(): SnapsDao
}