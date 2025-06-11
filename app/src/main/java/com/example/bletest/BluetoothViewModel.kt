package com.example.bletest

import android.bluetooth.le.ScanResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class BluetoothViewModel: ViewModel() {
    var selectedResult by mutableStateOf<ScanResult?>(null)
        private set

    fun selectResult(result: ScanResult) {
        selectedResult = result
    }
}