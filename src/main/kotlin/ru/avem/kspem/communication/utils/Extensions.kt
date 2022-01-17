package ru.avem.stand.utils

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import kotlin.NoSuchElementException
import kotlin.math.abs
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

fun <T> List<T>.second(): T {
    if (isEmpty() && size < 2) {
        throw NoSuchElementException("List invalid size.")
    }
    return this[1]
}

fun Any?.toStringOrDefault(default: String = "") = this?.let { this.toString() } ?: default
fun Any?.toStringOrNull() = if (this is String) this else null

fun String?.toIntOrDefaultByFormatter(default: Int) = toIntOrNullByFormatter() ?: default
fun String?.toIntOrNullByFormatter() = when {
    this == null -> null
    startsWith("0x") -> trim().replaceFirst("0x", "").toIntOrNull(16)
    startsWith("0b") -> trim().replaceFirst("0b", "").toIntOrNull(2)
    else -> trim().toIntOrNull()
}

fun String?.clearFracPart() = when {
    this == null -> null
    contains(".") -> cleanForParsing()?.split(".")?.first()
    contains(",") -> cleanForParsing()?.split(".")?.first()
    else -> this.cleanForParsing()
}

fun String?.toLongOrDefaultByFormatter(default: Long) = toLongOrNullByFormatter() ?: default
fun String?.toLongOrNullByFormatter() = when {
    this == null -> null
    startsWith("0x") -> cleanForParsing()?.replaceFirst("0x", "")?.toLongOrNull(16)
    startsWith("0b") -> cleanForParsing()?.replaceFirst("0b", "")?.toLongOrNull(2)
    else -> cleanForParsing()?.toLongOrNull()
}

fun String?.toBooleanOrDefault(default: Boolean) = toBooleanOrNull() ?: default
fun String?.toBooleanAndCheckByDefault(default: Boolean) = if (this == null) default else toBooleanOrNull()!! && default
fun Boolean.check(vararg flags: Boolean): Boolean {
    var result = this
    flags.forEach { result = result && it }
    return result
}

fun String?.toBooleanOrNull() = when {
    this == null -> null
    this.cleanForParsing() == "1" -> true
    this.cleanForParsing() == "0" -> false
    this.cleanForParsing() == "true" -> true
    this.cleanForParsing() == "false" -> false
    this.cleanForParsing() == "!false" -> true
    this.cleanForParsing() == "!true" -> false
    else -> null
}

fun String?.cleanForParsing(): String? = when {
    this == null -> null
    else -> replace(" ", "").replace(" ", "").replace(',', '.').toLowerCase()
}

fun String?.parseToFloatOrNull() = this?.cleanForParsing()?.toFloatOrNull()

fun String?.toDoubleOrDefault(default: Double) = this?.cleanForParsing()?.toDoubleOrNull() ?: default
fun String?.toFloatOrDefault(default: Float) = this?.cleanForParsing()?.toFloatOrNull() ?: default

fun Boolean.toLong() = if (this) 1L else 0L
fun Boolean.toInt() = if (this) 1 else 0
fun Boolean.toFloat() = if (this) 1f else 0f
fun Boolean.toDouble() = if (this) 1.0 else 0.0
fun Boolean.toByte() = toInt().toByte()

fun Float.toBoolean() = this != 0.0f
fun Int.toBoolean() = this != 0

fun String.isBoolean() = toBooleanOrNull() != null

fun String.autoformat(): String = this.toDoubleOrNull()?.autoformat() ?: this
fun Number.autoformat(): String = this.toDouble().autoformat()
fun Double.autoformat(): String =
    if (this.toLong().toDouble() == this) {
        "%d".format(Locale.ENGLISH, this.toLong())
    } else {
        with(abs(this)) {
            when {
                this == 0.0 -> "%.0f"
                this < 0.1f -> "%.3f"
                this < 1f -> "%.2f"
                this < 1000f -> "%.1f"
                else -> "%.0f"
            }.format(Locale.ENGLISH, this@autoformat)
        }
    }

@ExperimentalUnsignedTypes
fun UInt.toBoolean() = this != 0.toUInt()

@ExperimentalUnsignedTypes
fun UShort.toBoolean() = this != 0.toUShort()

fun Float.toOneIfZero() = if (this != 0f) this else 1f
fun Int.toOneIfZero() = if (this != 0) this else 1

fun Int.getRange(offset: Int, length: Int = 1) = (shr(offset) and getMask(length))
private fun getMask(length: Int) = (0xFFFFFFFF).shr(32 - length).toInt()

fun Int.putRange(index: Int, length: Int = 1, value: Int = 1): Int {
    var m = 0xFFFFFFFF shr (32 - length)
    m = m shl index
    m = m.inv()
    var res = this and m.toInt()
    val newM = value shl index
    res = res or newM
    return res
}

fun <T> Map<String, T>.anyFirst(vararg keys: String): T? {
    keys.forEach { if (this[it] != null) return this[it] }
    return null
}

inline fun <reified T> forValue(value: String = "", default: T? = null): T where T : Enum<T> {
    val allValues = mutableSetOf<String>()

    enumValues<T>().forEach { enumItem ->
        val availableValues = mutableListOf<String>()

        ((readInstanceProperty(enumItem, "name")) as String?)?.let { name ->
            availableValues.add(name)
            allValues.add(name)
        }

        ((readInstanceProperty(enumItem, "value")) as String?)?.let { value ->
            availableValues.add(value)
            allValues.add(value)
        }

        ((readInstanceProperty(enumItem, "aliases")) as List<String>?)?.let { aliases ->
            aliases.forEach { value ->
                availableValues.add(value)
                allValues.add(value)
            }
        }

        availableValues.firstOrNull {
            it.equals(value, true)
        }?.let {
            return enumItem
        }
    }

    if (default != null) return default

    throw Exception("Указанное значение [$value] не входит в поддерживаемый перечень:\n${allValues}")
}

@Suppress("UNCHECKED_CAST")
fun <R> readInstanceProperty(instance: Any, propertyName: String) =
    when {
        instance::class.memberProperties.any { it.name == propertyName } -> (instance::class.memberProperties.first { it.name == propertyName } as KProperty1<Any, *>).get(
            instance
        ) as R
        else -> null
    }

fun Pair<Byte, Byte>.toShort(order: ByteOrder = ByteOrder.BIG_ENDIAN) =
    ByteBuffer.allocate(2).order(order).put(first).put(second).also { it.flip() }.short

infix fun Double.minusPercent(percent: Double) = this - this * (percent * 0.01)

fun Byte.toHexString() = "0x%x".format(this)
fun Int.toHexString() = "0x%x".format(this)
fun Int.toHexValueString() = "%x".format(this)
