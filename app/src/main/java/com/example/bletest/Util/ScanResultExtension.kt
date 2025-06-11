package com.example.bletest.Util

import android.Manifest
import android.bluetooth.le.ScanResult
import androidx.annotation.RequiresPermission
import com.example.bletest.model.LionDevice


@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
fun ScanResult.getDisplayName(): String {
    val rawName = device.name
    return when {
        (rawName.isNullOrBlank() || rawName == "Unnamed device") -> scanRecord?.deviceName ?: "Unknown device"
        else -> rawName
    }
}

@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
fun ScanResult.isLionDevice(): Boolean {
    scanRecord?.deviceName?.let {
        if (LionDevice.from(it) != null) {
            return true
        }
    }
    return false
}
