package com.koredev.snap.data.source.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.koredev.snap.data.Snap
import javax.inject.Inject

class FirebaseApi @Inject constructor(
    val firestore: FirebaseFirestore
) {
    fun getSnaps(): LiveData<List<Snap>> {
        val snaps: MutableLiveData<List<Snap>> = MutableLiveData()

        firestore.collection("snaps").get().addOnCompleteListener {
            snaps.postValue(it.result as List<Snap>)
        }

        return snaps
    }

    fun saveSnap(snap: Snap) {
        firestore.collection("snaps").add(snap)
    }
}