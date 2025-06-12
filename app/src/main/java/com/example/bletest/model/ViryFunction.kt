package com.example.bletest.model

enum class ViryFunction(val value: UByte) {
    AllData(0x10u),
    BasicInfo(0x11u),
    ChargingInfo(0x61u),
    DischargingInfo(0x81u),
    DcDischargingInfo(0x83u),
    V12DischargingInfo(0x84u),
    AlarmInfo(0x91u),
    PowerControl(0xA1u),
    LcdControl(0xA2u),
    AcControl(0xB2u),
    UsbControl(0xB3u),
    V12Control(0xB4u),
    ;

    companion object {
        fun fromValue(value: UByte): ViryFunction? {
            return entries.find { it.value == value }
        }
    }
}
