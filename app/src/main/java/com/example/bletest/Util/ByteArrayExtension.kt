package com.example.bletest.Util

import java.nio.ByteBuffer
import java.nio.ByteOrder

fun ByteArray.toHexString() = joinToString(" ") { "%02x".format(it) }

fun ByteArray.toChunks(chunkSize: Int): List<ByteArray> {
    val result = mutableListOf<ByteArray>()
    var start = 0

    while (start < this.size) {
        val end = (start + chunkSize).coerceAtMost(this.size)
        val chunk = this.copyOfRange(start, end)
        result.add(chunk)
        start += chunkSize
    }

    return result
}

fun ByteArray.getBytes(
    from: Int,
    length: Int,
): ByteArray? {
    return if (from >= 0 && length > 0 && from + length <= this.size) {
        this.copyOfRange(from, from + length)
    } else {
        null
    }
}

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

fun ByteArray.getShort(
    from: Int,
    endianness: ByteOrder = ByteOrder.LITTLE_ENDIAN,
): Short? {
    return if (from >= 0 && from + 2 <= this.size) {
        ByteBuffer.wrap(this, from, 2)
            .order(endianness)
            .short
    } else {
        null
    }
}

fun ByteArray.getUInt(
    from: Int,
    endianness: ByteOrder = ByteOrder.LITTLE_ENDIAN,
): UInt? {
    return if (from >= 0 && from + 4 <= this.size) {
        ByteBuffer.wrap(this, from, 4)
            .order(endianness)
            .int
            .toUInt()
    } else {
        null
    }
}

fun ByteArray.startsWith(prefix: ByteArray): Boolean {
    if (this.size < prefix.size) return false
    for (i in prefix.indices) {
        if (this[i] != prefix[i]) return false
    }
    return true
}
