package com.example.bletest.model

enum class ViryAction(val value: UByte) {
    None(0x00u),
    PowerOn(0x01u),
    PowerOff(0x02u),
    Toggle(0x03u),
    ;

    companion object {
        fun fromValue(value: UByte): ViryAction? {
            return values().find { it.value == value }
        }
    }
}
