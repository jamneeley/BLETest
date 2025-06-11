package com.example.bletest

import android.Manifest
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.bletest.Util.BluetoothManagerWrapper
import com.example.bletest.Util.GattWrapper

class BluetoothViewModel(val context: Context) : ViewModel() {

    val bmWrapper = BluetoothManagerWrapper(context)

    var selectedResult by mutableStateOf<ScanResult?>(null)
        private set

    var gattWrapper by mutableStateOf<GattWrapper?>(null)
        private set


    fun selectResult(result: ScanResult) {
        selectedResult = result
    }

    fun connectToGatt() {
        if (context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            selectedResult?.device?.let {
                gattWrapper = GattWrapper(context = context, device = it)
                gattWrapper!!.connect()
            }
        }
    }
}