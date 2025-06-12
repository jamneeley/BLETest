package com.example.bletest.model

import android.bluetooth.BluetoothDevice

data class LionDevice(
    val type: LionDeviceType,
    val characteristics: Array<LionCharacteristic>
)
