package com.koredev.snap.data.source

import android.os.Environment
import androidx.lifecycle.LiveData
import com.koredev.snap.data.Snap
import com.koredev.snap.data.SnapRaw
import com.koredev.snap.data.source.local.SnapsDao
import com.koredev.snap.data.source.remote.FirebaseApi
import com.koredev.snap.util.NetworkUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class SnapsRepository @Inject constructor(
    val snapsDao: SnapsDao,
    val firestore: FirebaseApi,
    val networkUtil: NetworkUtil
) {

    companion object {
        const val TAG = "SnapsRepository"
    }

    fun getSnaps(limit: Int, offset: Int): LiveData<List<Snap>> {
        val hasConnection = networkUtil.isConnectedToInternet()
        return if (hasConnection) {
            getSnapsFromApi()
        } else {
            getSnapsFromDb(limit, offset)
        }
    }

    fun saveSnap(snap: SnapRaw) = GlobalScope.launch {
        val date = Calendar.getInstance().time.run {
            SimpleDateFormat("yyyy_MM_dd_HHmmss", Locale.getDefault()).format(this)
        }
        val directory = File("${Environment.getExternalStorageDirectory()}/Snap/")
        if (!directory.exists()) {
            directory.mkdir()
        }
        val file = File(Environment.getExternalStorageDirectory(), "Snap/snap_$date.jpeg")
        try {
            val stream = FileOutputStream(file.path)
            stream.write(snap.data)
            stream.close()
        } catch (ex: Exception) {
            Timber.e(TAG, ex)
        }

        saveSnapToApi(Snap(path = file.path))
        saveSnapToDb(Snap(path = file.path))
    }


    private fun getSnapsFromApi(): LiveData<List<Snap>> {
        return firestore.getSnaps()
    }

    private fun getSnapsFromDb(limit: Int, offset: Int): LiveData<List<Snap>> {
        return snapsDao.querySnaps(limit, offset)
    }

    private fun saveSnapToApi(snap: Snap) {
        firestore.saveSnap(snap)
    }

    private fun saveSnapToDb(snap: Snap) {
        snapsDao.insertSnap(snap)
    }
}