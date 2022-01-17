package ru.avem.kspem.communication.model.devices.avem.latr

import ru.avem.kspem.communication.model.IDeviceModel
import ru.avem.kspem.communication.model.DeviceRegister

class LatrModel : IDeviceModel {
    companion object {
        const val U_RMS_REGISTER = "Измеренное значение"
        const val MIN_END_STATUS_REGISTER = "Нижний концевик"
        const val MAX_END_STATUS_REGISTER = "Верхний концевик"
        const val VALUE_REGISTER = "Заданное напряжение"
        const val REGULATION_TIME_REGISTER = "Время выхода"
        const val CORRIDOR_REGISTER = "CORRIDOR_REGISTER"
        const val DELTA_REGISTER = "DELTA_REGISTER"
        const val MIN_VOLTAGE_LIMIT_REGISTER = "Заданное - это значение для точной"
        const val START_REGISTER = " START_REGISTER"
        const val STOP_REGISTER = " STOP_REGISTER"
        const val IR_TIME_PULSE_MAX_PERCENT = "IR_TIME_PULSE_MAX_PERCENT"
        const val IR_TIME_PULSE_MIN_PERCENT = "IR_TIME_PULSE_MIN_PERCENT"
        const val IR_DUTY_MAX_PERCENT = "IR_DUTY_MAX_PERCENT"
        const val IR_DUTY_MIN_PERCENT = "IR_DUTY_MIN_PERCENT"
        const val IR_TIME_PERIOD_MAX = "IR_TIME_PERIOD_MAX"
        const val IR_TIME_PERIOD_MIN = "IR_TIME_PERIOD_MIN"
        const val IR_TIMEOUT_RS = "IR_TIMEOUT_RS"
        const val STATUS = "STATUS"
    }

    override val registers: Map<String, DeviceRegister> = mapOf(
        STATUS to DeviceRegister(0x1024, DeviceRegister.RegisterValueType.INT32),
        U_RMS_REGISTER to DeviceRegister(0x1004, DeviceRegister.RegisterValueType.FLOAT),
        MIN_END_STATUS_REGISTER to DeviceRegister(0x1119, DeviceRegister.RegisterValueType.SHORT),
        MAX_END_STATUS_REGISTER to DeviceRegister(0x1118, DeviceRegister.RegisterValueType.SHORT),
        VALUE_REGISTER to DeviceRegister(0x111A, DeviceRegister.RegisterValueType.FLOAT),
        REGULATION_TIME_REGISTER to DeviceRegister(0x1120, DeviceRegister.RegisterValueType.INT32),
        IR_TIMEOUT_RS to DeviceRegister(0x1154, DeviceRegister.RegisterValueType.INT32),
        CORRIDOR_REGISTER to DeviceRegister(0x1122, DeviceRegister.RegisterValueType.FLOAT),
        DELTA_REGISTER to DeviceRegister(0x1124, DeviceRegister.RegisterValueType.FLOAT),
        MIN_VOLTAGE_LIMIT_REGISTER to DeviceRegister(0x112C, DeviceRegister.RegisterValueType.FLOAT),
        START_REGISTER to DeviceRegister(0x112E, DeviceRegister.RegisterValueType.SHORT),
        STOP_REGISTER to DeviceRegister(0x112F, DeviceRegister.RegisterValueType.SHORT),
        IR_TIME_PULSE_MAX_PERCENT to DeviceRegister(0x1156, DeviceRegister.RegisterValueType.FLOAT),
        IR_TIME_PULSE_MIN_PERCENT to DeviceRegister(0x1158, DeviceRegister.RegisterValueType.FLOAT),
        IR_DUTY_MAX_PERCENT to DeviceRegister(0x115A, DeviceRegister.RegisterValueType.FLOAT),
        IR_DUTY_MIN_PERCENT to DeviceRegister(0x115C, DeviceRegister.RegisterValueType.FLOAT),
        IR_TIME_PERIOD_MAX to DeviceRegister(0x115E, DeviceRegister.RegisterValueType.FLOAT),
        IR_TIME_PERIOD_MIN to DeviceRegister(0x1160, DeviceRegister.RegisterValueType.FLOAT)
    )

    override fun getRegisterById(idRegister: String) =
        registers[idRegister] ?: error("Такого регистра нет в описанной карте $idRegister")

}