package com.example.bletest

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.bletest.ui.theme.BLETestTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale

class MainActivity : ComponentActivity() {

    var bluetoothManager: BluetoothManager? = null
    var blueToothAdapter: BluetoothAdapter? = null

    val errorTag = "ERROR!"

    val permissions = arrayOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    val REQUEST_CODE = 1001
    var bluetoothDevices: MutableList<BluetoothDevice> = mutableListOf()

    private val scanCallback = object: ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)

            result?.device?.let { device ->
                bluetoothDevices.add(device)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.e(errorTag, "Scan failed with error code $errorCode")
        }
    }

    private fun makePermissionsRequest() {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
    }

    private fun initBLE() {
        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        blueToothAdapter = bluetoothManager?.adapter
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    private fun scanForBLE() {
        if (blueToothAdapter?.isEnabled == true) {
            blueToothAdapter!!.bluetoothLeScanner.startScan(scanCallback)
        } else {
            Log.d("", "Prompt user to turn on bluetooth")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBLE()

        makePermissionsRequest()

        setContent {
            BLETestTheme {
                BluetoothContentWithPermissions(
                    scanForBLE = { scanForBLE() }
                )
            }
        }
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BluetoothContentWithPermissions(
    scanForBLE: () -> Unit
) {

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    // Detect if user permanently denied any permission to show settings instructions
    val showSettingsInstructions = permissionsState.permissions.any {
        !it.status.isGranted && !it.status.shouldShowRationale
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            permissionsState.allPermissionsGranted -> {
                Button(onClick = { scanForBLE() }) {
                    Text("Scan for devices")
                }
            }
            showSettingsInstructions -> {
                Text("Please change Bluetooth settings for this app in system settings.")
            }
            else -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Need to grant permissions for Bluetooth")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { permissionsState.launchMultiplePermissionRequest() }) {
                        Text("Request Permissions")
                    }
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true)
@Composable
fun BluetoothContentWithPermissions_Preview() {
    BLETestTheme {
        // Just call it normally — but this won’t show correct UI due to no runtime permissions in preview
        BluetoothContentWithPermissions(
            scanForBLE = {}
        )
    }
}