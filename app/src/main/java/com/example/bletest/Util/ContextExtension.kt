package com.example.bletest.Util


import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun Context.hasPermission(type: String): Boolean {
    return ContextCompat.checkSelfPermission(this, type) == PackageManager.PERMISSION_GRANTED
}
