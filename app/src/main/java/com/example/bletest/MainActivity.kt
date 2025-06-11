package com.example.bletest

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale
import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.bletest.ui.theme.BLETestTheme

class MainActivity : ComponentActivity() {

    private lateinit var bmWrapper: BluetoothManagerWrapper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bmWrapper = BluetoothManagerWrapper(this)

        setContent {
            BLETestTheme {
                BluetoothContentWithPermissions(
                    wrapper = bmWrapper
                )
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BluetoothContentWithPermissions(
    wrapper: BluetoothManagerWrapper,
) {
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    // Auto-launch permission request on first composition
    LaunchedEffect(Unit) {
        permissionsState.launchMultiplePermissionRequest()
    }

    // Detect if user permanently denied any permission to show settings instructions
    val showSettingsInstructions = permissionsState.permissions.any {
        !it.status.isGranted && !it.status.shouldShowRationale
    }

    if (permissionsState.allPermissionsGranted) {
        BluetoothScreen(wrapper = wrapper)
    } else {
        BluetoothPermissionRequest(
            onRequestPermissions = { permissionsState.launchMultiplePermissionRequest() },
            showSettingsInstructions = showSettingsInstructions
        )
    }
}

@Composable
fun BluetoothPermissionRequest(
    onRequestPermissions: () -> Unit,
    showSettingsInstructions: Boolean
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (showSettingsInstructions) {
            Text("Please change Bluetooth permissions in system settings.")
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Bluetooth permissions are required")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onRequestPermissions) {
                    Text("Grant Permissions")
                }
            }
        }
    }
}

@Composable
fun BluetoothScreen(
    wrapper: BluetoothManagerWrapper,
) {

    val devices by wrapper.bluetoothDevices.collectAsState()
    val isScanning by wrapper.isScanning.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (!isScanning && devices.isEmpty()) {
                Text("Bluetooth is ready")
                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    wrapper.scanForBLE()
                }) {
                    Text("Scan for Devices")
                }
            } else {
                devices.forEach { device ->
                    Text(text = device.name ?: "Unnamed Device")
                }
            }
        }
    }
}