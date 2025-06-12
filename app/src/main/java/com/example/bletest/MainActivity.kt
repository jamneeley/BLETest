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
import android.bluetooth.le.ScanResult
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bletest.Util.calculateDistance
import com.example.bletest.Util.getDisplayName
import com.example.bletest.ui.theme.BLETestTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    data object Bluetooth : Screen("bluetooth")
    data object DeviceDetail : Screen("device_detail")
}

class MainActivity : ComponentActivity() {

    private lateinit var bluetoothViewModel: BluetoothViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        bluetoothViewModel = BluetoothViewModel(this)

        setContent {
            BLETestTheme {

                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = Screen.Bluetooth.route) {
                    composable(Screen.Bluetooth.route) {

                        val scope = rememberCoroutineScope()

                        BluetoothContentWithPermissions(
                            viewModel = bluetoothViewModel,
                            onResultClick = { result ->
                                scope.launch {
                                    bluetoothViewModel.selectResult(result)
                                    //need to stop scan when we connect to a device.
                                    navController.navigate(Screen.DeviceDetail.route)
                                }
                            }
                        )
                    }

                    composable(Screen.DeviceDetail.route) {
                        DeviceDetailScreen(bluetoothViewModel)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BluetoothContentWithPermissions(
    viewModel: BluetoothViewModel,
    onResultClick: (ScanResult) -> Unit
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
        BluetoothScreen(viewModel = viewModel, onResultClick = onResultClick)
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
    viewModel: BluetoothViewModel,
    onResultClick: (ScanResult) -> Unit
) {

    val results by viewModel.bluetoothManagerWrapper.scanResults.collectAsState()
    val isScanning by viewModel.bluetoothManagerWrapper.isScanning.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Button(
                modifier = Modifier.padding(16.dp),
                onClick = {
                    if (isScanning) viewModel.bluetoothManagerWrapper.stopScan() else viewModel.bluetoothManagerWrapper.scanForBLE()
                }
            ) {
                Text(if (isScanning) "Stop Scan" else "Start Scan")
            }

            if (isScanning) {
                Text("Scanning...")
            }

            HorizontalDivider(thickness = 8.dp)

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(results) { result ->
                    Button(onClick = {
                        onResultClick(result)
                    }) {
                        Text(result.getDisplayName())
                    }
                }
            }
        }
    }
}

@Composable
fun DeviceDetailScreen(
    viewModel: BluetoothViewModel,
) {

    LaunchedEffect(Unit) {

    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopStart
    ) {
        viewModel.selectedResult?.let {
            Text("Name: ${it.getDisplayName()}")
            Text("MAC: ${it.device.address}")
            Text("Distance: ${it.calculateDistance()}")
        }
    }
}