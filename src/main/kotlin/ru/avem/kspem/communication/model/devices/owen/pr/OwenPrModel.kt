package ru.avem.kspem.communication.model.devices.owen.pr

import ru.avem.kspem.communication.model.DeviceRegister
import ru.avem.kspem.communication.model.IDeviceModel

class OwenPrModel : IDeviceModel {
    companion object {
        const val RESET_DOG = "RESET_DOG"
        const val INPUTS_REGISTER = "STATES_BUTTONS_REGISTER"
        const val KMS1_REGISTER = "KMS1_REGISTER"
        const val KMS2_REGISTER = "KMS2_REGISTER"
        const val RES = "RES"
    }

    override val registers: Map<String, DeviceRegister> = mapOf(
        RESET_DOG to DeviceRegister(512, DeviceRegister.RegisterValueType.SHORT),
        INPUTS_REGISTER to DeviceRegister(513, DeviceRegister.RegisterValueType.SHORT),
        KMS1_REGISTER to DeviceRegister(516, DeviceRegister.RegisterValueType.SHORT),
        KMS2_REGISTER to DeviceRegister(517, DeviceRegister.RegisterValueType.SHORT),
        RES to DeviceRegister(518, DeviceRegister.RegisterValueType.SHORT)
    )

    override fun getRegisterById(idRegister: String) =
        registers[idRegister] ?: error("Такого регистра нет в описанной карте $idRegister")

    var outMask: Short = 0
}