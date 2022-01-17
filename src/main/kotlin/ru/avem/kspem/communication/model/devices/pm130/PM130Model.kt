package ru.avem.kspem.communication.model.devices.pm130

import ru.avem.kspem.communication.model.DeviceRegister
import ru.avem.kspem.communication.model.IDeviceModel

class PM130Model : IDeviceModel {
    companion object {
        const val F_REGISTER = "F_REGISTER"
        const val P_REGISTER = "P_REGISTER"
        const val Q_REGISTER = "Q_REGISTER"
        const val S_REGISTER = "S_REGISTER"
        const val U_AB_REGISTER = "U_AB_REGISTER"
        const val U_BC_REGISTER = "U_BC_REGISTER"
        const val U_CA_REGISTER = "U_CA_REGISTER"
        const val U_A_REGISTER = "U_AB_REGISTER"
        const val U_B_REGISTER = "U_BC_REGISTER"
        const val U_C_REGISTER = "U_CA_REGISTER"
        const val I_A_REGISTER = "I_A_REGISTER"
        const val I_B_REGISTER = "I_B_REGISTER"
        const val I_C_REGISTER = "I_C_REGISTER"
        const val COS_REGISTER = "COS_REGISTER"
    }

    override val registers: Map<String, DeviceRegister> = mapOf(
        F_REGISTER to DeviceRegister(0x1D4A, DeviceRegister.RegisterValueType.SHORT, coefficient = 1.0 / 100),
        P_REGISTER to DeviceRegister(0x3800, DeviceRegister.RegisterValueType.SHORT, coefficient = 1.0 / 1000),
        Q_REGISTER to DeviceRegister(0x3802, DeviceRegister.RegisterValueType.SHORT, coefficient = 1.0 / 1000),
        S_REGISTER to DeviceRegister(0x3804, DeviceRegister.RegisterValueType.SHORT, coefficient = 1.0 / 1000),
        U_A_REGISTER to DeviceRegister(0x3680, DeviceRegister.RegisterValueType.SHORT, coefficient = 1.0 / 10),
        U_B_REGISTER to DeviceRegister(0x3682, DeviceRegister.RegisterValueType.SHORT, coefficient = 1.0 / 10),
        U_C_REGISTER to DeviceRegister(0x3684, DeviceRegister.RegisterValueType.SHORT, coefficient = 1.0 / 10),
        I_A_REGISTER to DeviceRegister(0x3686, DeviceRegister.RegisterValueType.SHORT, coefficient = 1.0 / 100),
        I_B_REGISTER to DeviceRegister(0x3688, DeviceRegister.RegisterValueType.SHORT, coefficient = 1.0 / 100),
        I_C_REGISTER to DeviceRegister(0x368A, DeviceRegister.RegisterValueType.SHORT, coefficient = 1.0 / 100),
        U_AB_REGISTER to DeviceRegister(0x36BC, DeviceRegister.RegisterValueType.SHORT, coefficient = 1.0 / 10),
        U_BC_REGISTER to DeviceRegister(0x36BE, DeviceRegister.RegisterValueType.SHORT, coefficient = 1.0 / 10),
        U_CA_REGISTER to DeviceRegister(0x36C0, DeviceRegister.RegisterValueType.SHORT, coefficient = 1.0 / 10),
        COS_REGISTER to DeviceRegister(0x3586, DeviceRegister.RegisterValueType.SHORT, coefficient = 1.0 / 1000)
    )

    override fun getRegisterById(idRegister: String) =
        registers[idRegister] ?: error("Такого регистра нет в описанной карте $idRegister")
}
