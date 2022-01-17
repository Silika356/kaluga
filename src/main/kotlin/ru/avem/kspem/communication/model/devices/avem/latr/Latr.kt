package ru.avem.kspem.communication.model.devices.latr

import ru.avem.kserialpooler.communication.adapters.modbusrtu.ModbusRTUAdapter
import ru.avem.kserialpooler.communication.adapters.utils.ModbusRegister
import ru.avem.kserialpooler.communication.utils.TransportException
import ru.avem.kspem.communication.model.DeviceRegister
import ru.avem.kspem.communication.model.IDeviceController
import ru.avem.kspem.communication.model.devices.avem.latr.LatrModel
import ru.avem.kspem.communication.model.devices.avem.latr.LatrModel.Companion.CORRIDOR_REGISTER
import ru.avem.kspem.communication.model.devices.avem.latr.LatrModel.Companion.DELTA_REGISTER
import ru.avem.kspem.communication.model.devices.avem.latr.LatrModel.Companion.IR_DUTY_MAX_PERCENT
import ru.avem.kspem.communication.model.devices.avem.latr.LatrModel.Companion.IR_DUTY_MIN_PERCENT
import ru.avem.kspem.communication.model.devices.avem.latr.LatrModel.Companion.IR_TIME_PERIOD_MAX
import ru.avem.kspem.communication.model.devices.avem.latr.LatrModel.Companion.IR_TIME_PERIOD_MIN
import ru.avem.kspem.communication.model.devices.avem.latr.LatrModel.Companion.IR_TIME_PULSE_MAX_PERCENT
import ru.avem.kspem.communication.model.devices.avem.latr.LatrModel.Companion.IR_TIME_PULSE_MIN_PERCENT
import ru.avem.kspem.communication.model.devices.avem.latr.LatrModel.Companion.MIN_VOLTAGE_LIMIT_REGISTER
import ru.avem.kspem.communication.model.devices.avem.latr.LatrModel.Companion.REGULATION_TIME_REGISTER
import ru.avem.kspem.communication.model.devices.avem.latr.LatrModel.Companion.START_REGISTER
import ru.avem.kspem.communication.model.devices.avem.latr.LatrModel.Companion.STOP_REGISTER
import ru.avem.kspem.communication.model.devices.avem.latr.LatrModel.Companion.VALUE_REGISTER
import java.nio.ByteBuffer
import java.nio.ByteOrder


class Latr(
    override val name: String,
    override val protocolAdapter: ModbusRTUAdapter,
    override val id: Byte
) : IDeviceController {
    override var isResponding = false
    private val model = LatrModel()
    override var requestTotalCount = 0
    override var requestSuccessCount = 0
    override val pollingRegisters = mutableListOf<DeviceRegister>()
    override val writingRegisters = mutableListOf<Pair<DeviceRegister, Number>>()

    override fun readRegister(register: DeviceRegister) {
        isResponding = try {
            transactionWithAttempts {
                when (register.valueType) {
                    DeviceRegister.RegisterValueType.SHORT -> {
                        val modbusRegister =
                            protocolAdapter.readInputRegisters(id, register.address, 1).map(ModbusRegister::toShort)
                        register.value = modbusRegister.first().toDouble()
                    }
                    DeviceRegister.RegisterValueType.FLOAT -> {
                        val modbusRegister =
                            protocolAdapter.readInputRegisters(id, register.address, 2).map(ModbusRegister::toShort)
                        register.value =
                            ru.avem.kserialpooler.communication.utils.allocateOrderedByteBuffer(
                                modbusRegister,
                                ru.avem.kserialpooler.communication.utils.TypeByteOrder.BIG_ENDIAN,
                                4
                            ).float.toDouble()
                    }
                    DeviceRegister.RegisterValueType.INT32 -> {
                        val modbusRegister =
                            protocolAdapter.readInputRegisters(id, register.address, 2).map(ModbusRegister::toShort)
                        register.value =
                            ru.avem.kserialpooler.communication.utils.allocateOrderedByteBuffer(
                                modbusRegister,
                                ru.avem.kserialpooler.communication.utils.TypeByteOrder.BIG_ENDIAN,
                                4
                            ).int.toDouble()
                    }
                }
            }
            true
        } catch (e: TransportException) {
            false
        }
    }

    override fun readAllRegisters() {
        model.registers.values.forEach {
            readRegister(it)
        }
    }

    @Synchronized
    override fun <T : Number> writeRegister(register: DeviceRegister, value: T) {
        isResponding = try {
            when (value) {
                is Float -> {
                    val bb = ByteBuffer.allocate(4).putFloat(value).order(ByteOrder.BIG_ENDIAN)
                    val registers = listOf(ModbusRegister(bb.getShort(0)), ModbusRegister(bb.getShort(2)))
                    transactionWithAttempts {
                        protocolAdapter.presetMultipleRegisters(id, register.address, registers)
                    }
                }
                is Int -> {
                    val bb = ByteBuffer.allocate(4).putInt(value).order(ByteOrder.BIG_ENDIAN)
                    val registers = listOf(ModbusRegister(bb.getShort(0)), ModbusRegister(bb.getShort(2)))
                    transactionWithAttempts {
                        protocolAdapter.presetMultipleRegisters(id, register.address, registers)
                    }
                }
                is Short -> {
                    transactionWithAttempts {
                        protocolAdapter.presetSingleRegister(id, register.address, ModbusRegister(value))
                    }
                }
                else -> {
                    throw UnsupportedOperationException("Method can handle only with Float, Int and Short")
                }
            }
            true
        } catch (e: TransportException) {
            false
        }
    }

//    override fun writeRegisters(register: DeviceRegister, values: List<Short>) {
//        val registers = values.map { ModbusRegister(it) }
//        isResponding = try {
//            transactionWithAttempts {
//                protocolAdapter.presetMultipleRegisters(id, register.address, registers)
//            }
//            true
//        } catch (e: ru.avem.kserialpooler.communication.utils.TransportException) {
//            false
//        }
//    }

    override fun writeRegisters(register: DeviceRegister, values: List<Short>) {
        val registers = values.map { ModbusRegister(it) }
        isResponding = try {
            transactionWithAttempts {
                protocolAdapter.presetMultipleRegisters(id, register.address, registers)
            }
            true
        } catch (e: ru.avem.kserialpooler.communication.utils.TransportException) {
            false
        }
    }

    override fun checkResponsibility() {
        try {
            model.registers.values.firstOrNull()?.let {
                readRegister(it)
            }
        } catch (ignored: TransportException) {
        }
    }

    override fun getRegisterById(idRegister: String) = model.getRegisterById(idRegister)


    fun startUpLATR(voltage: Float, speedPerc: Float) {
        val corridor = 0.01f
        val delta = 0.01f
        val timeMinPulsePercent = 50.0f
        val timeMaxPulsePercent = 50.0f
//        val minDuttyPercent = speedPerc
//        val maxDuttyPercent = speedPerc
        val timeMinPeriod = 50f
        val timeMaxPeriod = 50f
        val minVoltage = 0.1f
        try {
            writeRegister(getRegisterById(VALUE_REGISTER), (voltage))
            writeRegister(getRegisterById(IR_TIME_PERIOD_MIN), (timeMinPulsePercent))
            writeRegister(getRegisterById(IR_TIME_PERIOD_MAX), (timeMaxPulsePercent))
            writeRegister(getRegisterById(IR_TIME_PULSE_MIN_PERCENT), (timeMinPeriod))
            writeRegister(getRegisterById(IR_TIME_PULSE_MAX_PERCENT), (timeMaxPeriod))
            writeRegister(getRegisterById(IR_DUTY_MIN_PERCENT), (speedPerc))
            writeRegister(getRegisterById(IR_DUTY_MAX_PERCENT), (speedPerc))
            writeRegister(getRegisterById(REGULATION_TIME_REGISTER), (300000))
            writeRegister(getRegisterById(CORRIDOR_REGISTER), (corridor))
            writeRegister(getRegisterById(DELTA_REGISTER), (delta))
            writeRegister(getRegisterById(MIN_VOLTAGE_LIMIT_REGISTER), (minVoltage))
            startLATR()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun startLATR() {
        try {
            writeRegister(getRegisterById(START_REGISTER), (1).toShort())
            writeRegister(getRegisterById(STOP_REGISTER), (0).toShort())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun startLATRUp() {
        try {
            writeRegister(getRegisterById(VALUE_REGISTER), (400f))
            writeRegister(getRegisterById(START_REGISTER), (1).toShort())
            writeRegister(getRegisterById(STOP_REGISTER), (0).toShort())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun startLATRDown() {
        try {
            writeRegister(getRegisterById(VALUE_REGISTER), (1f))
            writeRegister(getRegisterById(START_REGISTER), (1).toShort())
            writeRegister(getRegisterById(STOP_REGISTER), (0).toShort())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun startUpLATRPulse(voltage: Float, timePulsePercent: Float) {
        val corridor = 0.01f
        val delta = 0.01f
        val timeMinPeriod = 20.0f
        val timeMaxPeriod = 20.0f
        val minVoltage = 0.1f
        val minDuttyPercent = 80f
        val maxDuttyPercent = 80f
        try {
            writeRegister(getRegisterById(VALUE_REGISTER), (voltage))
            writeRegister(getRegisterById(IR_TIME_PERIOD_MIN), (timePulsePercent))
            writeRegister(getRegisterById(IR_TIME_PERIOD_MAX), (timePulsePercent))
            writeRegister(getRegisterById(IR_TIME_PULSE_MIN_PERCENT), (timeMinPeriod))
            writeRegister(getRegisterById(IR_TIME_PULSE_MAX_PERCENT), (timeMaxPeriod))
            writeRegister(getRegisterById(IR_DUTY_MIN_PERCENT), (minDuttyPercent))
            writeRegister(getRegisterById(IR_DUTY_MAX_PERCENT), (maxDuttyPercent))
            writeRegister(getRegisterById(REGULATION_TIME_REGISTER), (300000))
            writeRegister(getRegisterById(CORRIDOR_REGISTER), (corridor))
            writeRegister(getRegisterById(DELTA_REGISTER), (delta))
            writeRegister(getRegisterById(MIN_VOLTAGE_LIMIT_REGISTER), (minVoltage))
            startLATR()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopLATR() {
        try {
            writeRegister(getRegisterById(START_REGISTER), (0).toShort())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun resetLATR() {
        try {
            writeRegister(getRegisterById(START_REGISTER), (0x5A5A).toShort())
            writeRegister(getRegisterById(STOP_REGISTER), (0x5A5A).toShort())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

