package ru.avem.kspem.communication.model.devices.tilkom

import ru.avem.kspem.communication.model.DeviceRegister
import ru.avem.kspem.communication.model.IDeviceModel

class T42Model : IDeviceModel {
    companion object {
        const val TORQUE = "TORQUE"
        const val RPM = "RPM"
        const val AVERAGING = "AVERAGING"
    }

    override val registers: Map<String, DeviceRegister> = mapOf(
        TORQUE to DeviceRegister(0x0000, DeviceRegister.RegisterValueType.FLOAT),
        RPM to DeviceRegister(0x0002, DeviceRegister.RegisterValueType.FLOAT),
        AVERAGING to DeviceRegister(0x0001, DeviceRegister.RegisterValueType.SHORT)
    )

    override fun getRegisterById(idRegister: String) =
        registers[idRegister] ?: error("Такого регистра нет в описанной карте $idRegister")
}
