package com.example.bletest

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.bletest.Util.BluetoothManagerWrapper
import com.example.bletest.Util.GattConnectionHandler
import com.example.bletest.Util.getUByte
import com.example.bletest.model.ViryDeviceVariant
import com.example.bletest.model.ViryFunction
import com.example.bletest.model.ViryResponse

class BluetoothViewModel(val context: Context) : ViewModel() {

    val bluetoothManagerWrapper = BluetoothManagerWrapper(context)

    var selectedResult by mutableStateOf<ScanResult?>(null)
        private set

    var gattHandler by mutableStateOf<GattConnectionHandler?>(null)
        private set

    var response by mutableStateOf<ViryResponse.SafariBasicInfo?>(null)
        private set

    private fun processResponse(value: ByteArray): ViryResponse.SafariBasicInfo? {
        val functionByte = value.getUByte(3) ?: return null
        val function = ViryFunction.fromValue(functionByte)
        val variantByte = value.getUByte(1) ?: return null
        val variant = ViryDeviceVariant.fromValue(variantByte) ?: return null
        val message = value.copyOfRange(4, value.size - 1)

        return ViryResponse.SafariBasicInfo(variant, message)
    }

    @SuppressLint("MissingPermission")
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    suspend fun selectResult(result: ScanResult) {
        selectedResult = result
        bluetoothManagerWrapper.stopScan()
        connectToGatt()
        gattHandler!!.enableNotifications()
    }

    @SuppressLint("MissingPermission")
    private suspend fun connectToGatt() {
        selectedResult?.device?.let {
            gattHandler = GattConnectionHandler(context = context, device = it, onData = { it ->
                response = processResponse(it)
            })
            gattHandler!!.connect()
        }
    }
}