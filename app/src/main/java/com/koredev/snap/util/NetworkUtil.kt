package com.koredev.snap.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import javax.inject.Inject

class NetworkUtil @Inject constructor(
    private val context: Context
) {
    fun isConnectedToInternet(): Boolean {
        val connectivity = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        return connectivity.allNetworks.any { connectivity.getNetworkInfo(it).detailedState == NetworkInfo.State.CONNECTED }
    }
}