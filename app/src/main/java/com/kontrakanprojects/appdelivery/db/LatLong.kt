package com.kontrakanprojects.appdelivery.db

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LatLong(
    val latitude: Double,
    val longitude: Double,
) : Parcelable
