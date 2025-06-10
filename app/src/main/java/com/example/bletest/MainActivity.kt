package com.example.bletest

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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

class MainActivity : ComponentActivity() {

    val permissions = arrayOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    val REQUEST_CODE = 1001
    var hasPermissions by mutableStateOf(false)
    var showSettingsInstructions by mutableStateOf(false)

    private fun makePermissionsRequest() {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        makePermissionsRequest()

        setContent {
            BLETestTheme {
                BluetoothContent(hasPermissions, showSettingsInstructions) {
                    makePermissionsRequest()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)

        if (requestCode == REQUEST_CODE) {
            if (grantResults.contains(-1)) {
                val shouldShowRationaleForAny = permissions.any { permission ->
                    ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
                }
                showSettingsInstructions = !shouldShowRationaleForAny
            } else {
                hasPermissions = !grantResults.contains(-1)
            }
        }
    }
}

@Composable
private fun BluetoothContent(
    hasPermissions: Boolean,
    showSettingsInstructions: Boolean,
    makeRequest: () -> Unit
) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (hasPermissions) {
            Button(onClick = {

            }) {
                Text("Scan for devices")
            }
        } else if (showSettingsInstructions) {
            Text("Please change bluetooth settings for this app")
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Need to grant permissions for bluetooth")

                Text("You may")

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    makeRequest()
                }) {
                    Text("Request Permissions")
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true)
@Composable
fun TestListPreview() {
    BLETestTheme {
        Scaffold {
            BluetoothContent(false, false) { }
        }
    }
}

