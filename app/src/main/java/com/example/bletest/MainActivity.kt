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
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bletest.Util.BluetoothManagerWrapper
import com.example.bletest.Util.getDisplayName
import com.example.bletest.ui.theme.BLETestTheme

sealed class Screen(val route: String) {
    object Bluetooth : Screen("bluetooth")
    object DeviceDetail : Screen("device_detail/{deviceAddress}") {
        fun createRoute(address: String) = "device_detail/$address"
    }
}

class MainActivity : ComponentActivity() {

    private lateinit var bmWrapper: BluetoothManagerWrapper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bmWrapper = BluetoothManagerWrapper(this)

        setContent {
            BLETestTheme {

                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = Screen.Bluetooth.route) {
                    composable(Screen.Bluetooth.route) {
                        BluetoothContentWithPermissions(
                            wrapper = bmWrapper,
                            onDeviceClick = { device ->
                                navController.navigate(Screen.DeviceDetail.createRoute(device.address))
                            }
                        )
                    }

                    composable(
                        route = Screen.DeviceDetail.route,
                        arguments = listOf(navArgument("deviceAddress") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val deviceAddress = backStackEntry.arguments?.getString("deviceAddress")
                        DeviceDetailScreen(deviceAddress = deviceAddress)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BluetoothContentWithPermissions(
    wrapper: BluetoothManagerWrapper,
    onDeviceClick: (BluetoothDevice) -> Unit
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
        BluetoothScreen(wrapper = wrapper, onDeviceClick = onDeviceClick)
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
    onDeviceClick: (BluetoothDevice) -> Unit
) {

    val results by wrapper.scanResults.collectAsState()
    val isScanning by wrapper.isScanning.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = {
                    if (isScanning) wrapper.stopScan() else wrapper.scanForBLE()
                }
            ) {
                Text(if (isScanning) "Stop Scan" else "Start Scan")
            }

            if (isScanning) {
                Text("Scanning...")
            }

            Spacer(modifier = Modifier.height(16.dp))

            results.forEach { result ->
                Button(onClick = { onDeviceClick(result.device) }) {
                    Text(text = result.getDisplayName())
                }
            }
        }
    }
}

@Composable
fun DeviceDetailScreen(deviceAddress: String?) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Connected to device: $deviceAddress")
    }
}