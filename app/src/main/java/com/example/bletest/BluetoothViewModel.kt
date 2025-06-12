package com.example.bletest

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.bletest.Util.BluetoothManagerWrapper
import com.example.bletest.Util.GattConnectionHandler
import com.example.bletest.Util.getDeviceType
import com.example.bletest.Util.getUByte
import com.example.bletest.model.CharacteristicType
import com.example.bletest.model.ViryDeviceVariant
import com.example.bletest.model.ViryFunction
import com.example.bletest.model.ViryResponse
import java.nio.ByteBuffer

class BluetoothViewModel(val context: Context) : ViewModel() {

    val bluetoothManagerWrapper = BluetoothManagerWrapper(context)

    var selectedResult by mutableStateOf<ScanResult?>(null)
        private set

    var gattHandler by mutableStateOf<GattConnectionHandler?>(null)
        private set


    private fun processResponse(value: ByteArray): ViryResponse? {
        val functionByte = value.getUByte(3) ?: return null
        val function = ViryFunction.fromValue(functionByte)
        val variantByte = value.getUByte(1) ?: return null
        val variant = ViryDeviceVariant.fromValue(variantByte) ?: return null
        val message = value.copyOfRange(4, value.size - 1)

        return when (function) {
            ViryFunction.BasicInfo ->
                if (variant == ViryDeviceVariant.Summit) {
                    ViryResponse.SummitBasicInfo(variant, message)
                } else {
                    ViryResponse.SafariBasicInfo(variant, message)
                }
            ViryFunction.ChargingInfo -> ViryResponse.ChargingInfo(variant, message)
            ViryFunction.DischargingInfo -> ViryResponse.DischargingInfo(variant, message)
            ViryFunction.DcDischargingInfo -> {
                ViryResponse.DcDischargingInfo(variant, message)
            }
            ViryFunction.V12DischargingInfo -> ViryResponse.V12DischargingInfo(variant, message)
            ViryFunction.AlarmInfo -> ViryResponse.AlarmInfo(variant, message)
            ViryFunction.PowerControl -> ViryResponse.PowerControl(variant, message)
            ViryFunction.LcdControl -> ViryResponse.LcdControl(variant, message)
            ViryFunction.AcControl -> ViryResponse.AcControl(variant, message)
            ViryFunction.UsbControl -> ViryResponse.UsbControl(variant, message)
            ViryFunction.V12Control -> ViryResponse.V12Control(variant, message)
            else -> return null
        }
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
            gattHandler = GattConnectionHandler(context = context, device = it)
            gattHandler!!.connect()
        }
    }
}