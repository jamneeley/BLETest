package com.example.bletest.Util

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class GattConnectionHandler(val context: Context, val device: BluetoothDevice) {

    private var _gatt: BluetoothGatt? = null
    private var _mtu: Int = 23// lowest possible negotiated mtu.

    private var serviceConnected = CompletableDeferred<Boolean>()
    private var characteristics = mutableMapOf<UUID, BluetoothGattCharacteristic>()

    companion object {
        private const val GATT_MAX_MTU = 517
        private const val CCC_DESCRIPTOR_ID = "00002902-0000-1000-8000-00805f9b34fb"
        private const val CONNECTION_ATTEMPTS = 4
    }

    private val gattCallback = object : BluetoothGattCallback() {
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d("GATT", "Connected to GATT server.")
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d("GATT", "Disconnected from GATT server.")
            }
        }

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {

                for (service in gatt.services) {
                    for (char in service.characteristics) {
                        characteristics[char.uuid] = char
                    }
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
//            readResult.complete(value)
        }

        //Only called once - once the connection is made there wont be any more negotiations with the peripheral
        override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
            super.onMtuChanged(gatt, mtu, status)
            _mtu = mtu
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
//                readResult.complete(value)
            } else {
//                readResult.completeExceptionally(Exception("Read failed with status $status"))
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
//                writeResult.complete(true)
            } else {
//                writeResult.completeExceptionally(Exception("Read failed with status $status"))
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun enableNotifications() {
        val cccdUuid = UUID.fromString(CCC_DESCRIPTOR_ID)
        for (map in characteristics) {
            val payload = when {
                map.value.isIndicatable() -> BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
                map.value.isNotifiable() -> BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                else -> {
                    Log.e("ConnectionManager", "${map.key} doesn't support notifications/indications")
                    return
                }
            }

            map.value.getDescriptor(cccdUuid)?.let { cccDescriptor ->
                if (_gatt?.setCharacteristicNotification(map.value, true) == false) {
                    Log.e("ConnectionManager", "setCharacteristicNotification failed for ${map.key}")
                    return
                }
                _gatt!!.writeDescriptor(cccDescriptor, payload)
            } ?: Log.e("ConnectionManager", "${map.key} doesn't contain the CCC descriptor!")
        }

    }

    @SuppressLint("MissingPermission")
    fun disableNotifications() {
        for (map in characteristics) {
            val characteristic = map.value
            if (!characteristic.isNotifiable() && !characteristic.isIndicatable()) {
                Log.e(
                    "ConnectionManager",
                    "${map.key} doesn't support indications/notifications"
                )
                return
            }

            val cccdUuid = UUID.fromString(CCC_DESCRIPTOR_ID)
            characteristic.getDescriptor(cccdUuid)?.let { cccDescriptor ->
                if (_gatt?.setCharacteristicNotification(characteristic, false) == false) {
                    Log.e(
                        "ConnectionManager",
                        "setCharacteristicNotification failed for ${map.key}"
                    )
                    return
                }
                _gatt!!.writeDescriptor(
                    cccDescriptor,
                    BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                )
            } ?: Log.e(
                "ConnectionManager",
                "${map.key} doesn't contain the CCC descriptor!"
            )
        }
    }

//    @SuppressLint("MissingPermission")
//    suspend fun writeCharacteristic(serviceId: UUID, characteristicId: UUID, writeType: Int, payload: ByteArray): Boolean {
//        val char = _gatt?.getService(serviceId)?.getCharacteristic(characteristicId)
//        if (char == null || char.isWritable() == false) throw Exception("Failed to initiate characteristic read")
//
//        writeResult = CompletableDeferred() // Reset
//
//        _gatt!!.writeCharacteristic(char, payload, writeType)
//
//        return writeResult.await()
//    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    suspend fun connect() {
        _gatt = device.connectGatt(context, false, gattCallback)
        serviceConnected.await()
    }
}