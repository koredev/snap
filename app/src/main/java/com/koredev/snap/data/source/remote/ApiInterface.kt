package com.koredev.snap.data.source.remote

import androidx.lifecycle.LiveData
import com.koredev.snap.data.Snap

interface ApiInterface {

    fun getSnaps(): LiveData<List<Snap>>
}