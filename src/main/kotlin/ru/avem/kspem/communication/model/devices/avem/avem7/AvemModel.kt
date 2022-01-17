package ru.avem.kspem.communication.model.devices.avem.avem7

import ru.avem.kspem.communication.model.DeviceRegister
import ru.avem.kspem.communication.model.IDeviceModel

class AvemModel : IDeviceModel {
    companion object {
        const val VOLTAGE = "VOLTAGE"
        const val FREQUENCY = "FREQUENCY"
        const val SHUNT = "SHUNT"
        const val PGA_MODE = "PGA_MODE"
        const val RELAY_STATE = "RELAY_STATE"
        const val SERIAL_NUMBER = "SERIAL_NUMBER"
    }

    override val registers = mapOf(
        VOLTAGE to DeviceRegister(0x1004, DeviceRegister.RegisterValueType.FLOAT),
        FREQUENCY to DeviceRegister(0x1006, DeviceRegister.RegisterValueType.FLOAT),
        SHUNT to DeviceRegister(0x11A0, DeviceRegister.RegisterValueType.FLOAT),
        PGA_MODE to DeviceRegister(0x10C4, DeviceRegister.RegisterValueType.SHORT),
        RELAY_STATE to DeviceRegister(0x1136, DeviceRegister.RegisterValueType.SHORT),
        SERIAL_NUMBER to DeviceRegister(0x1108, DeviceRegister.RegisterValueType.SHORT)
    )

    override fun getRegisterById(idRegister: String) =
        registers[idRegister] ?: error("Такого регистра нет в описанной карте $idRegister")
}
