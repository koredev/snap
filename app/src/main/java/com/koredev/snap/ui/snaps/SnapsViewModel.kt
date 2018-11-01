package com.koredev.snap.ui.snaps

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.koredev.snap.data.source.local.SnapsDao
import javax.inject.Inject

class SnapsViewModel @Inject constructor(
    val firebase: FirebaseFirestore,
    val room: SnapsDao
) : ViewModel() {
}