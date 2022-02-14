package ru.avem.kspem.communication.model.devices.avem.phaseMeter

import ru.avem.kspem.communication.model.DeviceRegister
import ru.avem.kspem.communication.model.IDeviceModel

class PhaseMeterModel : IDeviceModel {
    companion object {
        const val ulPeriodTime0 = "ulPeriodTime0"
        const val ulImpulsTime0 = "ulImpulsTime0"
        const val ulPeriodTime1 = "ulPeriodTime1"
        const val ulImpulsTime1 = "ulImpulsTime1"
        const val ulCPUTimeDelay = "ulCPUTimeDelay"
        const val uiWindingGroup0 = "uiWindingGroup0"
        const val uiWindingGroup1 = "uiWindingGroup1"
        const val uiStartStop = "uiStartStop"
        const val ulIDSerialNumber = "ulIDSerialNumber"
        const val measureTime = "measureTime"
    }

    override val registers: Map<String, DeviceRegister> = mapOf(
        ulPeriodTime0 to DeviceRegister(0x0002, DeviceRegister.RegisterValueType.INT32),
        ulImpulsTime0 to DeviceRegister(0x0004, DeviceRegister.RegisterValueType.INT32),
        ulPeriodTime1 to DeviceRegister(0x0006, DeviceRegister.RegisterValueType.INT32),
        ulImpulsTime1 to DeviceRegister(0x0008, DeviceRegister.RegisterValueType.INT32),
        ulCPUTimeDelay to DeviceRegister(0x000A, DeviceRegister.RegisterValueType.INT32),
        uiWindingGroup0 to DeviceRegister(0x000C, DeviceRegister.RegisterValueType.SHORT),
        uiWindingGroup1 to DeviceRegister(0x000D, DeviceRegister.RegisterValueType.SHORT),
        uiStartStop to DeviceRegister(0x000E, DeviceRegister.RegisterValueType.SHORT),
        ulIDSerialNumber to DeviceRegister(0x000F, DeviceRegister.RegisterValueType.INT32),
        measureTime to DeviceRegister(0x0011, DeviceRegister.RegisterValueType.FLOAT)
    )

    override fun getRegisterById(idRegister: String) =
        registers[idRegister] ?: error("Такого регистра нет в описанной карте $idRegister")
}
