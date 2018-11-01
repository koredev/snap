package com.koredev.snap.ui.snaps

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.koredev.snap.di.Injectable
import com.koredev.snap.util.showSystemUi

class SnapsFragment : Fragment(), Injectable {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        showSystemUi()
    }
}