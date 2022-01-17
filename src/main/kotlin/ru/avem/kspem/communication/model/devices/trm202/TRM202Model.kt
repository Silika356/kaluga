package ru.avem.kspem.communication.model.devices.trm202

import ru.avem.kspem.communication.model.DeviceRegister
import ru.avem.kspem.communication.model.IDeviceModel

class TRM202Model : IDeviceModel {
    companion object {
        const val T_1 = "T_1"
        const val T_2 = "T_2"
    }

    override val registers: Map<String, DeviceRegister> = mapOf(
        T_1 to DeviceRegister(0x1009, DeviceRegister.RegisterValueType.FLOAT),
        T_2 to DeviceRegister(0x100B, DeviceRegister.RegisterValueType.FLOAT)
    )

    override fun getRegisterById(idRegister: String) =
        registers[idRegister] ?: error("Такого регистра нет в описанной карте $idRegister")
}
