package com.koredev.snap.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.koredev.snap.data.Snap

@Dao
interface SnapsDao {
    @Query("SELECT * FROM snap ORDER BY date limit :limit offset :offset")
    fun querySnaps(limit: Int, offset: Int): LiveData<List<Snap>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSnap(snap: Snap)
}