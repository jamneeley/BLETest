package com.example.bletest.Util
import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import com.example.bletest.model.LionDeviceType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BluetoothManagerWrapper(context: Context) {

    private val bluetoothManager: BluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

    private val _bluetoothDevices = MutableStateFlow<List<ScanResult>>(emptyList())
    val scanResults: StateFlow<List<ScanResult>> = _bluetoothDevices

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning

    private val scanCallback = object : ScanCallback() {

        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.let {
                result.device?.let { device ->
                    if (context.hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                        val currentList = _bluetoothDevices.value
                        if (currentList.none { it.device.address == device.address }) {
                            if (result.isConnectable && result.getDeviceType() == LionDeviceType.Safari) {
                                _bluetoothDevices.value = currentList + result
                            }
                        }
                    }
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            _isScanning.value = false
            Log.e("BluetoothManagerWrapper", "Scan failed: $errorCode")
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun scanForBLE() {
        if (bluetoothAdapter?.isEnabled == true) {
            val scanner = bluetoothAdapter.bluetoothLeScanner

// Create your filters, for example filtering by device name or service UUID
            val filters = listOf(
                ScanFilter.Builder()
//                    .setDeviceName("MyDeviceName")  // example filter by name
                    //.setServiceUuid(ParcelUuid.fromString("0000180D-0000-1000-8000-00805f9b34fb")) // filter by UUID
                    .build()
            )

            val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY) //Scan for a brief period
                .setScanMode(ScanSettings.MATCH_MODE_STICKY) //Scan for close proximity devices
                .build()


            _isScanning.value = true
            scanner.startScan(filters, settings, scanCallback)
        } else {
            Log.w("BluetoothManagerWrapper", "Bluetooth is disabled.")
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun stopScan() {
        bluetoothAdapter?.bluetoothLeScanner?.stopScan(scanCallback)
        _isScanning.value = false
    }
}