package com.example.bletest.model

import java.util.UUID


enum class DeviceCategory {
    Sanctuary,
    LithiumBattery,
    Generator,
}


enum class LionDeviceType {
    Sanctuary,
    UT1300,
    UT3500,
    Summit,
    Safari,
    ;

    val category: DeviceCategory
        get() =
            when (this) {
                Sanctuary -> DeviceCategory.Sanctuary
                UT1300 -> DeviceCategory.LithiumBattery
                UT3500 -> DeviceCategory.LithiumBattery
                Summit -> DeviceCategory.Generator
                Safari -> DeviceCategory.Generator
            }

    val getLionDevice: LionDevice
        get() =
            when (this) {
                Sanctuary -> LionDevice(this, arrayOf())
                UT1300 -> LionDevice(this, arrayOf())
                UT3500 -> LionDevice(this, arrayOf())
                Summit -> LionDevice(this, arrayOf())
                Safari -> LionDevice(this, arrayOf(
                    LionCharacteristic(UUID.fromString("00001800-0000-1000-8000-00805f9b34fb"), UUID.fromString("00002a00-0000-1000-8000-00805f9b34fb"), CharacteristicType.String),
                    LionCharacteristic(UUID.fromString("00001800-0000-1000-8000-00805f9b34fb"), UUID.fromString("00002a01-0000-1000-8000-00805f9b34fb"), CharacteristicType.String),
                    LionCharacteristic(UUID.fromString("00001800-0000-1000-8000-00805f9b34fb"), UUID.fromString("00002a04-0000-1000-8000-00805f9b34fb"), CharacteristicType.String),

                    LionCharacteristic(UUID.fromString("00001801-0000-1000-8000-00805f9b34fb"), UUID.fromString("00002a05-0000-1000-8000-00805f9b34fb"), CharacteristicType.int),

                    LionCharacteristic(UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb"), UUID.fromString("00002a50-0000-1000-8000-00805f9b34fb"), CharacteristicType.macAddress),

                    LionCharacteristic(UUID.fromString("0000ff00-0000-1000-8000-00805f9b34fb"), UUID.fromString("0000ff01-0000-1000-8000-00805f9b34fb"), CharacteristicType.int),
                    LionCharacteristic(UUID.fromString("0000ff00-0000-1000-8000-00805f9b34fb"), UUID.fromString("0000ff02-0000-1000-8000-00805f9b34fb"), CharacteristicType.int),

                    LionCharacteristic(UUID.fromString("00010203-0405-0607-0809-0a0b0c0d1912"), UUID.fromString("00010203-0405-0607-0809-0a0b0c0d2b12"), CharacteristicType.int)
                ))
            }

    companion object {
        fun from(name: String): LionDeviceType? {
            val lowercased = name.lowercase()
            return if (lowercased.contains("summit")) {
                Summit
            } else if (lowercased.contains("safari")) {
                Safari
            } else if (lowercased.contains("1300")) {
                UT1300
            } else if (lowercased.contains("ut3500")) {
                UT3500
            } else if (lowercased.contains("sanctuary")) {
                Sanctuary
            } else {
                null
            }
        }
    }
}
