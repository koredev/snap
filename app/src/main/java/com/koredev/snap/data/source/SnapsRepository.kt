package com.koredev.snap.data.source

import androidx.lifecycle.LiveData
import com.koredev.snap.data.Snap
import com.koredev.snap.data.source.local.SnapsDao
import com.koredev.snap.data.source.remote.ApiInterface
import com.koredev.snap.util.NetworkUtil
import javax.inject.Inject

class SnapsRepository @Inject constructor(
    val apiInterface: ApiInterface,
    val snapsDao: SnapsDao,
    val networkUtil: NetworkUtil
) {

    fun getSnaps(limit: Int, offset: Int): LiveData<List<Snap>> {
        val hasConnection = networkUtil.isConnectedToInternet()
        return if (hasConnection) {
            getSnapsFromApi()
        } else {
            getSnapsFromDb(limit, offset)
        }
    }

    fun getSnapsFromApi(): LiveData<List<Snap>> {
        return apiInterface.getSnaps()
    }

    fun getSnapsFromDb(limit: Int, offset: Int): LiveData<List<Snap>> {
        return snapsDao.querySnaps(limit, offset)
    }
}