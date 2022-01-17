package ru.avem.stand.modules.r.communication.model.devices.avem.ikas

import ru.avem.kserialpooler.communication.adapters.modbusrtu.ModbusRTUAdapter
import ru.avem.kserialpooler.communication.adapters.utils.ModbusRegister
import ru.avem.kserialpooler.communication.utils.TransportException
import ru.avem.kspem.communication.model.DeviceController
import ru.avem.kspem.communication.model.DeviceRegister
import ru.avem.kspem.communication.model.devices.avem.ikas.IKAS8Model
import ru.avem.kspem.communication.model.devices.avem.ikas.IKAS8Model.Companion.CFG_SCHEME
import ru.avem.kspem.communication.model.devices.avem.ikas.IKAS8Model.Companion.START_STOP
import ru.avem.stand.utils.second
import java.lang.Thread.sleep
import java.nio.ByteBuffer

class IKAS8(
    override val name: String,
    override val protocolAdapter: ModbusRTUAdapter,
    override val id: Byte
) : DeviceController() {
    val model = IKAS8Model()
    override var requestTotalCount = 0
    override var requestSuccessCount = 0
    override val pollingRegisters = mutableListOf<DeviceRegister>()

    override val writingRegisters = mutableListOf<Pair<DeviceRegister, Number>>()

    override fun readRegister(register: DeviceRegister) {
        isResponding = try {
            transactionWithAttempts {
                register.value = when (register.valueType) {
                    DeviceRegister.RegisterValueType.SHORT -> {
                        protocolAdapter.readInputRegisters(id, register.address, 1).map(ModbusRegister::toShort).first()
                    }
                    DeviceRegister.RegisterValueType.FLOAT -> {
                        val modbusRegister =
                            protocolAdapter.readInputRegisters(id, register.address, 2).map(ModbusRegister::toShort)
                        ByteBuffer.allocate(4).putShort(modbusRegister.first()).putShort(modbusRegister.second())
                            .also { it.flip() }.float
                    }
                    DeviceRegister.RegisterValueType.INT32 -> {
                        val modbusRegister =
                            protocolAdapter.readInputRegisters(id, register.address, 2).map(ModbusRegister::toShort)
                        ByteBuffer.allocate(4).putShort(modbusRegister.first()).putShort(modbusRegister.second())
                            .also { it.flip() }.int
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
                    val bb = ByteBuffer.allocate(4).putFloat(value).also { it.flip() }
                    val registers = listOf(ModbusRegister(bb.short), ModbusRegister(bb.short))
                    transactionWithAttempts {
                        protocolAdapter.presetMultipleRegisters(id, register.address, registers)
                    }
                }
                is Int -> {
                    val bb = ByteBuffer.allocate(4).putInt(value).also { it.flip() }
                    val registers = listOf(ModbusRegister(bb.short), ModbusRegister(bb.short))
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

    override fun writeRegisters(register: DeviceRegister, values: List<Short>) {
        val registers = values.map { ModbusRegister(it) }
        isResponding = try {
            transactionWithAttempts {
                protocolAdapter.presetMultipleRegisters(id, register.address, registers)
            }
            true
        } catch (e: TransportException) {
            false
        }
    }

    override fun checkResponsibility() {
            readRegister(model.registers.values.first())
    }

    override fun getRegisterById(idRegister: String) = model.getRegisterById(idRegister)

    fun startMeasuringAB() {
        writeRegister(getRegisterById(CFG_SCHEME), IKAS8Model.Scheme.AB.value)
        writeRegister(getRegisterById(START_STOP), 1)
        sleep(2000)
    }

    fun startMeasuringBC() {
        writeRegister(getRegisterById(CFG_SCHEME), IKAS8Model.Scheme.BC.value)
        writeRegister(getRegisterById(START_STOP), 1)
        sleep(2000)
    }

    fun startMeasuringCA() {
        writeRegister(getRegisterById(CFG_SCHEME), IKAS8Model.Scheme.CA.value)
        writeRegister(getRegisterById(START_STOP), 1)
        sleep(2000)
    }
}
