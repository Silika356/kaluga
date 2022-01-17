package ru.avem.kspem.communication.model

import java.util.*

class DeviceRegister(
    val address: Short,
    val valueType: RegisterValueType,
    val unit: String = "",
    val coefficient: Double = 1.0
) : Observable() {
    enum class RegisterValueType {
        SHORT,
        FLOAT,
        INT32
    }

    var value: Number = 0.0
        set(value) {
            field = value.toDouble() * coefficient
            setChanged()
            notifyObservers(field)
        }
}
