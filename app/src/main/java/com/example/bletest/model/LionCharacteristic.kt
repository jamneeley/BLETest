package com.example.bletest.model

import java.util.UUID

enum class CharacteristicType {
    String,
    macAddress,
    int,
    float
}

data class LionCharacteristic(
//    val name: String,
    val serviceUUID: UUID,
    val uuid: UUID,
    val type: CharacteristicType
)