package ru.avem.kspem.communication.model.devices.avem.ikas

import ru.avem.kspem.communication.model.DeviceRegister
import ru.avem.kspem.communication.model.IDeviceModel

class IKAS8Model : IDeviceModel {
    companion object {
        const val STATUS = "STATUS"
        const val RESIST_MEAS = "RESIST_MEAS"
        const val START_STOP = "START_STOP"
        const val CFG_SCHEME = "CFG_SCHEME"
        const val CFG_CURRENT_RANGE = "CFG_CURRENT_RANGE"
        const val CFG_RESIST_RANGE = "CFG_RESIST_RANGE"
    }

    enum class Scheme(val value: Int) {
        AA(0x47),
        BB(0x42),
        CC(0x43),
        AB(0x46),
        BC(0x44),
        CA(0x45)
    }

    override val registers: Map<String, DeviceRegister> = mapOf(
        STATUS to DeviceRegister(
            0x1000,
            DeviceRegister.RegisterValueType.INT32
        ), // 0x00h=Завершено,0x65=Ожидание,0x80=Ошибка,0x81=Ошибка (АЦП),0x82=Ошибка (шунт),0x83=Оошибка (ток),0x84=Ошибка (напряжение),404=Измерение
        RESIST_MEAS to DeviceRegister(0x1002, DeviceRegister.RegisterValueType.FLOAT),
        START_STOP to DeviceRegister(0x10C8, DeviceRegister.RegisterValueType.INT32),
        CFG_SCHEME to DeviceRegister(0x10CA, DeviceRegister.RegisterValueType.INT32),
        CFG_CURRENT_RANGE to DeviceRegister(
            0x10CC,
            DeviceRegister.RegisterValueType.INT32
        ), // 0x00 = 20А, 0x01 = 0.5А, 0x02 = 1А, 0x03 = 2.5А, 0x04 = 5А, 0x05 = 10А
        CFG_RESIST_RANGE to DeviceRegister(
            0x10CE,
            DeviceRegister.RegisterValueType.INT32
        ) // 0x00 = ???, 0x01 =< 8Ом, 0x02 =< 200Ом, 0x03 =< 25кОм
    )

    override fun getRegisterById(idRegister: String) =
        registers[idRegister] ?: error("Такого регистра нет в описанной карте $idRegister")
}
