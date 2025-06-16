package com.example.bletest.Util

import android.Manifest
import android.bluetooth.le.ScanResult
import androidx.annotation.RequiresPermission
import com.example.bletest.model.LionDeviceType
import kotlin.math.pow


@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
fun ScanResult.getDisplayName(): String {
    val rawName = device.name
    return when {
        (rawName.isNullOrBlank() || rawName == "Unnamed device") -> scanRecord?.deviceName ?: "Unknown device"
        else -> rawName
    }
}

@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
fun ScanResult.getDeviceType(): LionDeviceType? {
    scanRecord?.deviceName?.let {
        return LionDeviceType.from(it)
    }
    return null
}

fun ScanResult.calculateDistance(): Double {
    val txPower = -59 //the expected rssi at 1 meter
    val pathLossExponent = 3.5 // 2.0 for free space, 2.7-4.0 for indoors

    return 10.0.pow((txPower - rssi) / (10 * pathLossExponent))
}