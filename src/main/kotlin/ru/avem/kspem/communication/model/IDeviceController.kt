package ru.avem.kspem.communication.model

import mu.KotlinLogging
import ru.avem.kserialpooler.communication.adapters.AdapterInterface
import ru.avem.kserialpooler.communication.utils.TransportException
import java.lang.Thread.sleep

interface IDeviceController {
    val name: String

    val protocolAdapter: AdapterInterface

    val id: Byte

    var isResponding: Boolean

    var requestTotalCount: Int
    var requestSuccessCount: Int

    fun readRegister(register: DeviceRegister) {

    }

    fun readRequest(request: String): String {
        return ""
    }

    fun <T : Number> writeRegister(register: DeviceRegister, value: T) {

    }

    fun readAllRegisters() {

    }

    fun writeRegisters(register: DeviceRegister, values: List<Short>) {

    }

    fun writeRequest(request: String) {

    }

    val pollingRegisters: MutableList<DeviceRegister>
    val writingRegisters: MutableList<Pair<DeviceRegister, Number>>

    fun IDeviceController.transactionWithAttempts(block: () -> Unit) {
        var attempt = 0
        while (true) {
            requestTotalCount++

            try {
                block()
                requestSuccessCount++
                break
            } catch (e: TransportException) {
                val message =
                    "repeat $attempt/${protocolAdapter.connection.attemptCount} attempts with common success rate = ${(requestSuccessCount) * 100 / requestTotalCount}%"
                KotlinLogging.logger(name).info(message)

                if (attempt++ >= protocolAdapter.connection.attemptCount) {
                    throw e
                }
            }
            sleep(10)
        }
    }

    fun getRegisterById(idRegister: String): DeviceRegister

    fun addPollingRegister(register: DeviceRegister) {
        synchronized(protocolAdapter.connection) {
            pollingRegisters.add(register)
        }
    }

    fun addWritingRegister(writingPair: Pair<DeviceRegister, Number>) {
        synchronized(protocolAdapter.connection) {
            writingRegisters.add(writingPair)
        }
    }

    fun removePollingRegister(register: DeviceRegister) {
        synchronized(protocolAdapter.connection) {
            pollingRegisters.remove(register)
        }
    }

    fun removeAllPollingRegisters() {
        synchronized(protocolAdapter.connection) {
            pollingRegisters.forEach(DeviceRegister::deleteObservers)
            pollingRegisters.clear()
        }
    }

    fun removeAllWritingRegisters() {
        synchronized(protocolAdapter.connection) {
            writingRegisters.map {
                it.first
            }.forEach(DeviceRegister::deleteObservers)
            writingRegisters.clear()
        }
    }

    fun readPollingRegisters() {
        synchronized(protocolAdapter.connection) {
            for (register in pollingRegisters) {
                isResponding = try {
                    readRegister(register)
                    true
                } catch (e: TransportException) {
                    false
                }
                if (!isResponding) break
            }
        }
    }

    fun writeWritingRegisters() {
        synchronized(protocolAdapter.connection) {
            for (pair in writingRegisters) {
                isResponding = try {
                    writeRegister(pair.first, pair.second)
                    true
                } catch (e: TransportException) {
                    false
                }
                if (!isResponding) break
            }
        }
    }

    fun checkResponsibility()
}
