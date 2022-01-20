package ru.avem.kspem.communication.model

import ru.avem.kserialpooler.communication.Connection
import ru.avem.kserialpooler.communication.adapters.modbusrtu.ModbusRTUAdapter
import ru.avem.kserialpooler.communication.utils.SerialParameters
import ru.avem.kspem.app.Main.Companion.isAppRunning
import ru.avem.kspem.communication.adapters.serial.SerialAdapter
import ru.avem.kspem.communication.adapters.stringascii.StringASCIIAdapter
import ru.avem.kspem.communication.model.devices.avem.avem7.Avem
import ru.avem.kspem.communication.model.devices.cs02021.CS02021
import ru.avem.kspem.communication.model.devices.delta.Delta
import ru.avem.kspem.communication.model.devices.gpt.GPT
import ru.avem.kspem.communication.model.devices.latr.Latr
import ru.avem.kspem.communication.model.devices.owen.pr.OwenPr
import ru.avem.kspem.communication.model.devices.pm130.PM130
import ru.avem.kspem.communication.model.devices.th01.TH01
import ru.avem.kspem.communication.model.devices.tilkom.T42
import ru.avem.kspem.communication.model.devices.tilkom.T42Model
import ru.avem.kspem.communication.model.devices.trm202.TRM202
import ru.avem.stand.modules.r.communication.model.devices.avem.ikas.IKAS8
import java.lang.Thread.sleep
import kotlin.concurrent.thread

object CommunicationModel {
    @Suppress("UNUSED_PARAMETER")
    enum class DeviceID(description: String) {
        DD2_1("ПР200-24.4.2.0"),
        PH_1("Фазометр"),
        PV21("АВЭМ-4 Uлин."),
        PV22("АВЭМ-4 ДПР"),
        PR61("ИКАС-8"),
        PS81("ТРМ202 - Термометр - окр. воздух"),
        M42("Датчик момента"),
        PRV89("ЦС0202-1"),
        T42("Преобразователь датчика момента Т42"),
        UZ91("ЧП Delta 1.5кВт"),
    }
    var isConnected = false

    var connection = Connection(
        adapterName = "CP2103 USB to RS-485",
        serialParameters = SerialParameters(8, 0, 1, 38400),
        timeoutRead = 100,
        timeoutWrite = 100
    ).apply {
        connect()
        isConnected = true
    }

//    var isConnectedGPT = false

//    var connectionGPT = Connection(
//        adapterName = "CP2103 USB to GPT",
//        serialParameters = SerialParameters(8, 0, 1, 9600),
//        timeoutRead = 100,
//        timeoutWrite = 100
//    ).apply {
//        connect()
//        isConnectedGPT = true
//    }

//    var isConnectedDelta = false
//    private val connectionDelta = Connection(
//        adapterName = "CP2103 USB to DELTA",
//        serialParameters = SerialParameters(8, 0, 1, 38400),
//        timeoutRead = 100,
//        timeoutWrite = 100
//    ).apply {
//        connect()
//        isConnectedDelta = true
//    }

    private val modbusAdapter = ModbusRTUAdapter(connection)
//    private val deltaAdapter = ModbusRTUAdapter(connectionDelta)
    private val gptAdapter = SerialAdapter(connection)
//    private val modbusAdapterCP2000 = ModbusRTUAdapter(connectionCP2000)

    private val devices: Map<DeviceID, IDeviceController> = mapOf(
        DeviceID.DD2_1 to OwenPr(DeviceID.DD2_1.toString(), modbusAdapter, 2),
//        DeviceID.PH_1 to OwenPr(DeviceID.DD2_1.toString(), modbusAdapter, 4),
        DeviceID.PV21 to Avem(DeviceID.PV21.toString(), modbusAdapter, 21),
        DeviceID.PV22 to Avem(DeviceID.PV22.toString(), modbusAdapter, 22),
        DeviceID.PR61 to IKAS8(DeviceID.PR61.toString(), modbusAdapter, 61),
        DeviceID.PS81 to TRM202(DeviceID.PS81.toString(), modbusAdapter, 81),
        DeviceID.M42 to T42(DeviceID.M42.toString(), modbusAdapter, 42),
        DeviceID.PRV89 to CS02021 (DeviceID.PRV89.toString(), gptAdapter, 1),
        DeviceID.UZ91 to Delta(DeviceID.UZ91.toString(), modbusAdapter, 91)
    )

    init {
        thread(isDaemon = true) {
            while (isAppRunning) {
                if (isConnected) {
                    devices.values.forEach {
                        it.readPollingRegisters()
                    }
                }
                sleep(1)
            }
        }
        thread(isDaemon = true) {
            while (isAppRunning) {
                if (isConnected) {
                    devices.values.forEach {
                        it.writeWritingRegisters()
                    }
                }
                sleep(1)
            }
        }
    }

    fun getDeviceById(deviceID: DeviceID) = devices[deviceID] ?: error("Не определено $deviceID")

    fun startPoll(deviceID: DeviceID, registerID: String, block: (Number) -> Unit) {
        val device = getDeviceById(deviceID)
        val register = device.getRegisterById(registerID)
        register.addObserver { _, arg ->
            block(arg as Number)
        }
        device.addPollingRegister(register)
    }

    fun <T : IDeviceController> device(deviceID: DeviceID): T {
        return devices[deviceID] as T
    }

    fun clearPollingRegisters() {
        devices.values.forEach(IDeviceController::removeAllPollingRegisters)
        devices.values.forEach(IDeviceController::removeAllWritingRegisters)
    }

    fun stopPoll(device: DeviceID) {
        getDeviceById(device).removeAllPollingRegisters()
    }

    fun clearReadingRegisters() {
        devices.values.forEach(IDeviceController::removeAllPollingRegisters)
    }

    fun checkDevices(): List<DeviceID> {
        devices.values.forEach(IDeviceController::checkResponsibility)
        return devices.filter { !it.value.isResponding }.keys.toList()
    }

    fun addWritingRegister(deviceID: DeviceID, registerID: String, value: Number) {
        val device = getDeviceById(deviceID)
        val register = device.getRegisterById(registerID)
        device.addWritingRegister(register to value)
    }
}
