package com.example.bletest.Util

import android.Manifest
import android.bluetooth.le.ScanResult
import androidx.annotation.RequiresPermission


@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
fun ScanResult.getDisplayName(): String {
    val rawName = device.name
    return when {
        (rawName.isNullOrBlank() || rawName == "Unnamed device") -> scanRecord?.deviceName ?: "Unknown device"
        else -> rawName
    }
}
