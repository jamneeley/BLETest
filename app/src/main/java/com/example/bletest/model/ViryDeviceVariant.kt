package com.example.bletest.model

enum class ViryDeviceVariant(val value: UByte) {
    Summit(0x17u),
    Quest(0x18u),
    Ppg(0x31u),
    Safari(0x39u),
    Ut1300(0x51u),
    Ut1400(0x55u),
    SafariExpansion(0x58u),
    ;

    companion object {
        fun fromValue(value: UByte): ViryDeviceVariant? {
            return entries.find { it.value == value }
        }
    }
}
