package com.example.bletest.model

import com.example.bletest.Util.getByte
import com.example.bletest.Util.getUByte
import com.example.bletest.Util.getUShort

sealed interface ViryResponse {
    val variant: ViryDeviceVariant
    val function: ViryFunction
    val message: ByteArray

    companion object {
        fun from(packet: ByteArray): ViryResponse? {
            val functionByte = packet.getUByte(3) ?: return null
            val function = ViryFunction.fromValue(functionByte) ?: return null
            val variantByte = packet.getUByte(1) ?: return null
            val variant = ViryDeviceVariant.fromValue(variantByte) ?: return null
            val message = packet.copyOfRange(4, packet.size - 1)

            return when (function) {
                ViryFunction.BasicInfo ->
                    if (variant == ViryDeviceVariant.Summit) {
                        SummitBasicInfo(variant, message)
                    } else {
                        SafariBasicInfo(variant, message)
                    }
                ViryFunction.ChargingInfo -> ChargingInfo(variant, message)
                ViryFunction.DischargingInfo -> DischargingInfo(variant, message)
                ViryFunction.DcDischargingInfo -> {
                    DcDischargingInfo(variant, message)
                }
                ViryFunction.V12DischargingInfo -> V12DischargingInfo(variant, message)
                ViryFunction.AlarmInfo -> AlarmInfo(variant, message)
                ViryFunction.PowerControl -> PowerControl(variant, message)
                ViryFunction.LcdControl -> LcdControl(variant, message)
                ViryFunction.AcControl -> AcControl(variant, message)
                ViryFunction.UsbControl -> UsbControl(variant, message)
                ViryFunction.V12Control -> V12Control(variant, message)
                else -> return null
            }
        }
    }

    data class ChargingInfo(
        override val variant: ViryDeviceVariant,
        override val message: ByteArray,
    ) : ViryResponse {
        override val function = ViryFunction.ChargingInfo

        val chargeState: Boolean? = message.getUByte(0)?.toInt()?.equals(1)
        val chargePower: UShort? = message.getUShort(1)
        val dcChargeState: OutletState? = message.getUByte(3)?.let { OutletState.fromValue(it) }
        val dcChargePower: UShort? = message.getUShort(4)
        val acChargeState: OutletState? = message.getUByte(6)?.let { OutletState.fromValue(it) }
        val acChargePower: UShort? = message.getUShort(7)
        val typeCChargeState: OutletState? = message.getUByte(9)?.let { OutletState.fromValue(it) }
        val typeCChargePower: UShort? = message.getUShort(10)
    }

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

    data class SummitBasicInfo(
        override val variant: ViryDeviceVariant,
        override val message: ByteArray,
    ) : ViryResponse {
        override val function = ViryFunction.BasicInfo

        val batteryState: Boolean? = message.getByte(0)?.toInt()?.equals(1)
        val batteryPercent: UByte? = message.getByte(1)?.toUByte()
        val internalBatteryState: Boolean? = message.getUByte(2)?.toInt()?.equals(1)
        val internalBatteryPercent: UByte? = message.getUByte(3)
        val externalBatteryState: Boolean? = message.getUByte(4)?.toInt()?.equals(1)
        val externalBatteryPercent: UByte? = message.getUByte(5)
        val timeType: TimeType? = message.getUByte(7)?.let { TimeType.fromValue(it) }
        val timeHours: UShort? = message.getUShort(8)
        val timeMinutes: UByte? = message.getUByte(10)

        enum class TimeType(val value: UByte) {
            None(0x00u),
            Discharging(0x01u),
            Charging(0x02u),
            ;

            companion object {
                fun fromValue(value: UByte) = entries.find { it.value == value }
            }
        }
    }

    data class DischargingInfo(
        override val variant: ViryDeviceVariant,
        override val message: ByteArray,
    ) : ViryResponse {
        override val function = ViryFunction.DischargingInfo

        val dischargeState: Boolean? = message.getByte(0)?.toInt()?.equals(1)
        val dischargePower: UShort? = message.getUShort(1)
        val dcDischargeState: OutletState? = message.getUByte(3)?.let { OutletState.fromValue(it) }
        val dcDischargePower: UShort? = message.getUShort(4)
        val acDischargeState: OutletState? = message.getUByte(6)?.let { OutletState.fromValue(it) }
        val acDischargePower: UShort? = message.getUShort(7)
        val acDischargeSource: AcDischargeSource? =
            message.getUByte(9)?.let {
                AcDischargeSource.fromValue(
                    it,
                )
            }

        enum class AcDischargeSource(val value: UByte) {
            NoPowerOut(0x00u),
            Inverter(0x01u),
            Grid(0x02u),
            ;

            companion object {
                fun fromValue(value: UByte) = entries.find { it.value == value }
            }
        }
    }

    data class DcDischargingInfo(
        override val variant: ViryDeviceVariant,
        override val message: ByteArray,
    ) : ViryResponse {
        override val function = ViryFunction.DcDischargingInfo
        val v12DischargeState: OutletState? = message.getUByte(0)?.let { OutletState.fromValue(it) }
        val v12DischargePower: UShort? = message.getUShort(1)
        val usbA1DischargeState: OutletState? = message.getUByte(3)?.let { OutletState.fromValue(it) }
        val usbA1DischargePower: UByte? = message.getUByte(4)
        val usbA2DischargeState: OutletState? = message.getUByte(5)?.let { OutletState.fromValue(it) }
        val usbA2DischargePower: UByte? = message.getUByte(6)
        val typeC1DischargeState: OutletState? = message.getUByte(7)?.let { OutletState.fromValue(it) }
        val typeC1DischargePower: UShort? = message.getUShort(8)
        val typeC2DischargeState: OutletState? =
            message.getUByte(10)?.let {
                OutletState.fromValue(
                    it,
                )
            }
        val typeC2DischargePower: UShort? = message.getUShort(11)
    }

    data class V12DischargingInfo(
        override val variant: ViryDeviceVariant,
        override val message: ByteArray,
    ) : ViryResponse {
        override val function = ViryFunction.V12DischargingInfo

        val v12DischargeState: OutletState? = message.getUByte(0)?.let { OutletState.fromValue(it) }
        val v12DischargePower: UShort? = message.getUShort(1)
    }

    data class AlarmInfo(
        override val variant: ViryDeviceVariant,
        override val message: ByteArray,
    ) : ViryResponse {
        override val function = ViryFunction.AlarmInfo

        val errorCode: ErrorCode? = message.getUByte(0)?.let { ErrorCode.fromValue(it) }
        val acError: Boolean? = message.getUByte(2)?.toInt()?.equals(1)
        val v12Error: Boolean? = message.getUByte(3)?.toInt()?.equals(1)
        val usbA1Error: Boolean? = message.getUByte(5)?.toInt()?.equals(1)
        val usbA2Error: Boolean? = message.getUByte(6)?.toInt()?.equals(1)
        val typeC1Error: Boolean? = message.getUByte(7)?.toInt()?.equals(1)
        val typeC2Error: Boolean? = message.getUByte(8)?.toInt()?.equals(1)

        enum class ErrorCode(val value: UByte) {
            None(0x00u),
            LowVoltage(0x0Cu),
            LowPower(0x0Du),
            V12PortError(0x24u),
            InverterError(0x3Du),
            UsbC1PortError(0x47u),
            UsbC2PortError(0x49u),
            UsbA1PortError(0x4Bu),
            UsbA2PortError(0x4Du),
            ExpansionLowVoltage(0x54u),
            ExpansionLowPower(0x55u),
            ExpansionDataLoss(0x58u),
            ;

            companion object {
                fun fromValue(value: UByte): ErrorCode? = entries.find { it.value == value }
            }
        }
    }

    data class LcdControl(
        override val variant: ViryDeviceVariant,
        override val message: ByteArray,
    ) : ViryResponse {
        override val function = ViryFunction.LcdControl

        val lcdFeedback: Feedback? = message.getUByte(0)?.let { Feedback.fromValue(it) }
    }

    data class AcControl(
        override val variant: ViryDeviceVariant,
        override val message: ByteArray,
    ) : ViryResponse {
        override val function = ViryFunction.AcControl

        val acFeedback: Feedback? = message.getUByte(0)?.let { Feedback.fromValue(it) }
    }

    data class UsbControl(
        override val variant: ViryDeviceVariant,
        override val message: ByteArray,
    ) : ViryResponse {
        override val function = ViryFunction.UsbControl

        val usbFeedback: Feedback? = message.getUByte(0)?.let { Feedback.fromValue(it) }
    }

    data class V12Control(
        override val variant: ViryDeviceVariant,
        override val message: ByteArray,
    ) : ViryResponse {
        override val function = ViryFunction.V12Control

        val v12Feedback: Feedback? = message.getUByte(0)?.let { Feedback.fromValue(it) }
    }

    data class PowerControl(
        override val variant: ViryDeviceVariant,
        override val message: ByteArray,
    ) : ViryResponse {
        override val function = ViryFunction.PowerControl

        val powerFeedback: Feedback? = message.getUByte(0)?.let { Feedback.fromValue(it) }
    }

    enum class OutletState(val value: UByte) {
        Off(0x00u),
        OnNoPower(0x01u),
        On(0x02u),
        ;

        companion object {
            fun fromValue(value: UByte) = entries.find { it.value == value }
        }
    }

    enum class Feedback(val value: UByte) {
        NoAction(0x00u),
        Success(0x01u),
        Failure(0x02u),
        ;

        companion object {
            fun fromValue(value: UByte) = entries.find { it.value == value }
        }
    }
}
