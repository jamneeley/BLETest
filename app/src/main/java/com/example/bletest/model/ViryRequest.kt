package com.example.bletest.model

import java.util.UUID

sealed interface ViryRequest {
    val variant: ViryDeviceVariant
    val function: ViryFunction
    val message: ByteArray
    val packet: ByteArray
        get() {
            val length = (message.size + 5).toUByte()
            val header = ViryHeader.Devicebound.value
            val variantValue = variant.value
            val functionValue = function.value

            val packet = byteArrayOf(header.toByte(), variantValue.toByte(), length.toByte(), functionValue.toByte()) + message
            val checksum = (packet.sumOf { it.toUInt() } % 256u).toByte()

            return (packet + checksum)
        }
    val id: UUID

    data class AllData(override val variant: ViryDeviceVariant) : ViryRequest {
        override val function = ViryFunction.AllData
        override val message: ByteArray = byteArrayOf(0x00, 0x00, 0x00, 0x00)
        override val id: UUID = UUID.randomUUID()
    }

    data class BasicInfo(override val variant: ViryDeviceVariant) : ViryRequest {
        override val function = ViryFunction.BasicInfo
        override val message: ByteArray = byteArrayOf(0x00, 0x00, 0x00, 0x00)
        override val id: UUID = UUID.randomUUID()
    }

    data class ChargingInfo(override val variant: ViryDeviceVariant) : ViryRequest {
        override val function = ViryFunction.ChargingInfo
        override val message: ByteArray = byteArrayOf(0x00, 0x00, 0x00, 0x00)
        override val id: UUID = UUID.randomUUID()
    }

    data class DischargingInfo(override val variant: ViryDeviceVariant) : ViryRequest {
        override val function = ViryFunction.DischargingInfo
        override val message: ByteArray = byteArrayOf(0x00, 0x00, 0x00, 0x00)
        override val id: UUID = UUID.randomUUID()
    }

    data class DcDischargingInfo(override val variant: ViryDeviceVariant) : ViryRequest {
        override val function = ViryFunction.DcDischargingInfo
        override val message: ByteArray = byteArrayOf(0x00, 0x00, 0x00, 0x00)
        override val id: UUID = UUID.randomUUID()
    }

    data class V12DischargingInfo(override val variant: ViryDeviceVariant) : ViryRequest {
        override val function = ViryFunction.V12DischargingInfo
        override val message: ByteArray = byteArrayOf(0x00, 0x00, 0x00, 0x00)
        override val id: UUID = UUID.randomUUID()
    }

    data class AlarmInfo(override val variant: ViryDeviceVariant) : ViryRequest {
        override val function = ViryFunction.AlarmInfo
        override val message: ByteArray = byteArrayOf(0x00, 0x00, 0x00, 0x00)
        override val id: UUID = UUID.randomUUID()
    }

    data class PowerControl(
        override val variant: ViryDeviceVariant,
        val action: ViryAction,
    ) : ViryRequest {
        override val function = ViryFunction.PowerControl
        override val message: ByteArray = byteArrayOf(action.value.toByte(), 0x00, 0x00, 0x00)
        override val id: UUID = UUID.randomUUID()
    }

    data class LcdControl(
        override val variant: ViryDeviceVariant,
        val action: ViryAction,
    ) : ViryRequest {
        override val function = ViryFunction.LcdControl
        override val message: ByteArray = byteArrayOf(action.value.toByte(), 0x00, 0x00, 0x00)
        override val id: UUID = UUID.randomUUID()
    }

    data class AcControl(
        override val variant: ViryDeviceVariant,
        val action: ViryAction,
    ) : ViryRequest {
        override val function = ViryFunction.AcControl
        override val message: ByteArray = byteArrayOf(action.value.toByte(), 0x00, 0x00, 0x00)
        override val id: UUID = UUID.randomUUID()
    }

    data class UsbControl(
        override val variant: ViryDeviceVariant,
        val action: ViryAction,
    ) : ViryRequest {
        override val function = ViryFunction.UsbControl
        override val message: ByteArray = byteArrayOf(action.value.toByte(), 0x00, 0x00, 0x00)
        override val id: UUID = UUID.randomUUID()
    }

    data class V12Control(
        override val variant: ViryDeviceVariant,
        val action: ViryAction,
    ) : ViryRequest {
        override val function = ViryFunction.V12Control
        override val message: ByteArray = byteArrayOf(action.value.toByte(), 0x00, 0x00, 0x00)
        override val id: UUID = UUID.randomUUID()
    }
}
