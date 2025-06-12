package com.example.bletest.model

import com.example.bletest.Util.getUByte
import com.example.bletest.Util.getUShort

sealed interface ViryResponse {
    val variant: ViryDeviceVariant
    val function: ViryFunction
    val message: ByteArray

    data class SafariBasicInfo(
        override val variant: ViryDeviceVariant,
        override val message: ByteArray,
    ) : ViryResponse {
        override val function = ViryFunction.BasicInfo

        val batteryState: Boolean? = message.getUByte(0)?.toInt()?.equals(1)
        val batteryPercent: UByte? = message.getUByte(1)
        val internalBatteryState: Boolean? = message.getUByte(2)?.toInt()?.equals(1)
        val internalBatteryPercent: UByte? = message.getUByte(3)
        val externalBatteryState: Boolean? = message.getUByte(4)?.toInt()?.equals(1)
        val externalBatteryPercent: UByte? = message.getUByte(5)
        val systemState = message.getUByte(6)?.let { SystemState.fromValue(it) }
        val timeHours: UShort? = message.getUShort(7)
        val timeMinutes: UByte? = message.getUByte(9)
        val inTimeHours: UByte? = message.getUByte(10)
        val outTimeHours: UByte? = message.getUByte(11)

        enum class SystemState(val value: UByte) {
            PowerOff(0x00u),
            Standby(0x01u),
            Discharging(0x02u),
            Balanced(0x03u),
            Charging(0x04u),
            Full(0x05u),
            ;

            companion object {
                fun fromValue(value: UByte) = entries.find { it.value == value }
            }
        }
    }
}
