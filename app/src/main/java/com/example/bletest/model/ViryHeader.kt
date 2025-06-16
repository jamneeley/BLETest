package com.example.bletest.model

enum class ViryHeader(val value: UByte) {
    Devicebound(0xF6u),
    Appbound(0xF7u),
    ;

    companion object {
        fun fromValue(value: UByte): ViryHeader? {
            return entries.find { it.value == value }
        }
    }
}
