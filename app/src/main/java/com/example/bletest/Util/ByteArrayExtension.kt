package com.example.bletest.Util

import java.nio.ByteBuffer
import java.nio.ByteOrder


fun ByteArray.getByte(at: Int): Byte? {
    return if (at in this.indices) this[at] else null
}

fun ByteArray.getUByte(at: Int): UByte? {
    return if (at in this.indices) this[at].toUByte() else null
}

fun ByteArray.getUShort(
    from: Int,
    endianness: ByteOrder = ByteOrder.LITTLE_ENDIAN,
): UShort? {
    return if (from >= 0 && from + 2 <= this.size) {
        ByteBuffer.wrap(this, from, 2)
            .order(endianness)
            .short
            .toUShort()
    } else {
        null
    }
}
