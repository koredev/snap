package com.koredev.snap.ui.snap

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.koredev.snap.data.source.local.SnapsDao
import com.koredev.snap.data.Snap
import javax.inject.Inject

class SnapViewModel @Inject constructor(
    val firebase: FirebaseFirestore,
    val room: SnapsDao
) : ViewModel() {

    fun save(snap: Snap) {
        firebase.collection("snaps").add(snap)
        room.insertSnap(snap)
    }
}