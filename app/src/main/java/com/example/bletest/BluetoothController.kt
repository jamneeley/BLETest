package com.example.bletest
import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission

class BluetoothController(context: Context) {

    private val bluetoothManager: BluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

    val bluetoothDevices: MutableList<BluetoothDevice> = mutableListOf()

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.device?.let { device ->
                if (!bluetoothDevices.contains(device)) {
                    bluetoothDevices.add(device)
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e("BluetoothController", "Scan failed: $errorCode")
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun scanForBLE() {
        if (bluetoothAdapter?.isEnabled == true) {
            bluetoothAdapter.bluetoothLeScanner.startScan(scanCallback)
        } else {
            Log.w("BluetoothController", "Bluetooth is disabled.")
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun stopScan() {
        bluetoothAdapter?.bluetoothLeScanner?.stopScan(scanCallback)
    }
}