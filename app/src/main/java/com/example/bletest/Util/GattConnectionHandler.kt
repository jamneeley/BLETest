package com.example.bletest.Util

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothStatusCodes
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import com.example.bletest.model.ViryConstants
import kotlinx.coroutines.CompletableDeferred
import java.util.UUID

class GattConnectionHandler(val context: Context, val device: BluetoothDevice, val onData: (ByteArray) -> Unit) {

    private var _gatt: BluetoothGatt? = null
    private var _mtu: Int = 23// lowest possible negotiated mtu.

    private var serviceConnected = CompletableDeferred<Boolean>()
    private var notifyCharacteristic: BluetoothGattCharacteristic? = null
    private var writeCharacteristic: BluetoothGattCharacteristic? = null

    companion object {
        private const val CCC_DESCRIPTOR_ID = "00002902-0000-1000-8000-00805f9b34fb"
    }

    private val gattCallback = object : BluetoothGattCallback() {
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d("GATT", "Connected to GATT server.")
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                disconnect()
                Log.d("GATT", "Disconnected from GATT server.")
            }
        }

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {

                val allChar = gatt.services.flatMap { it.characteristics }
                val notifyChar = allChar.firstOrNull { it.uuid == ViryConstants.readCharacteristicId }
                notifyChar?.let {
                    notifyCharacteristic = it
                }

                val writeChar = allChar.firstOrNull { it.uuid == ViryConstants.writeCharacteristicId }
                writeChar?.let {
                    writeCharacteristic = it
                }

                serviceConnected.complete(true)
            } else {
                serviceConnected.complete(false)
                _gatt?.close()
                _gatt = null
                Log.d("", "${status}") //error is shown through the status
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            onData(value)
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
        }

        //Only called once - once the connection is made there wont be any more negotiations with the peripheral
        override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
            super.onMtuChanged(gatt, mtu, status)
            _mtu = mtu
        }

    }

    @SuppressLint("MissingPermission")
    fun enableReadNotifications() {
        val cccdUuid = UUID.fromString(CCC_DESCRIPTOR_ID)
        notifyCharacteristic?.let {
            val payload = when {
                it.isIndicatable() -> BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
                it.isNotifiable() -> BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                else -> {
                    Log.e("ConnectionManager", "${it.uuid} doesn't support notifications/indications")
                    return
                }
            }

            it.getDescriptor(cccdUuid)?.let { cccDescriptor ->
                if (_gatt?.setCharacteristicNotification(it, true) == false) {
                    Log.e("ConnectionManager", "setCharacteristicNotification failed for ${it.uuid}")
                    return
                }
                _gatt!!.writeDescriptor(cccDescriptor, payload)
            } ?: Log.e("ConnectionManager", "${it.uuid} doesn't contain the CCC descriptor!")
            println()
        }
    }

    @SuppressLint("MissingPermission")
    fun send(value: ByteArray) {
        writeCharacteristic?.let {

            val writeType =
                when {
                    it.isWritable() -> BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                    it.isWritableWithoutResponse() -> BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
                    else -> {
                        Log.e("ConnectionManager", "${it.uuid} doesn't support notifications/indications")
                        return
                    }
                }
            val status = _gatt?.writeCharacteristic(it, value, writeType)
            if (status != BluetoothStatusCodes.SUCCESS) {
                Log.e("ERROR", "")
                return
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    suspend fun connect() {
        _gatt = device.connectGatt(context, false, gattCallback)
        serviceConnected.await()
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        serviceConnected.complete(false)
        _gatt?.disconnect()
        _gatt?.close()
        _gatt = null
    }
}