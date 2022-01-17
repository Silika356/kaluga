package ru.avem.kspem.communication.model.devices.gpt

import ru.avem.kserialpooler.communication.utils.TransportException
import ru.avem.kspem.communication.adapters.stringascii.StringASCIIAdapter
import ru.avem.kspem.communication.model.DeviceController
import ru.avem.kspem.communication.model.DeviceRegister
import java.util.*

class GPT(
    override val name: String,
    override val protocolAdapter: StringASCIIAdapter,
    override val id: Byte
) : DeviceController() {
    private val model = GPTModel()
    override var requestTotalCount = 0
    override var requestSuccessCount = 0
    override val pollingRegisters = mutableListOf<DeviceRegister>()

    override val writingRegisters = mutableListOf<Pair<DeviceRegister, Number>>()

    private lateinit var mode: Mode

    enum class Mode {
        ACW,
        DCW,
        IR
    }

    override fun readRequest(request: String): String {
            return protocolAdapter.read(request)
    }

    override fun readAllRegisters() {
        model.registers.values.forEach {
            readRegister(it)
        }
    }

    override fun writeRequest(request: String) {
        protocolAdapter.write(request)
    }

    override fun checkResponsibility() {
        isResponding = try {
            val ans = readRequest("MEAS?")
            ans.isNotEmpty()
        } catch (ignored: TransportException) {
            false
        }
    }

    override fun getRegisterById(idRegister: String) = model.getRegisterById(idRegister)

    fun remoteControl() {
        writeRequest("*idn?")
    }

    fun setMode(mode: Mode) {
        this.mode = mode
        writeRequest("MANU:EDIT:MODE $mode")
    }

    fun setVoltage(voltage: Double) {
        writeRequest("MANU:$mode:VOLTage ${"%.3f".format(Locale.US, voltage)}")
    }

    fun setMaxAmperage(amperage: Double) {
        writeRequest("MANU:$mode:CHIS ${"%.3f".format(Locale.US, amperage)}")
    }

    fun setRiseTime(timeRise: Double) {
        writeRequest("MANU:RTIM $timeRise")
    }

    fun setFreq(frequency: Double) {
        writeRequest("MANU:$mode:FREQ $frequency")
    }

    fun setTestTime(time: Double) {
        writeRequest("MANU:$mode:TTIMe $time")
    }

    fun onTest() {
        writeRequest("FUNC:TEST ON")
    }

    fun offTest() {
        writeRequest("FUNC:TEST OFF")
    }

    fun setNameTest(nameTest: String) {
        writeRequest("MANU:NAME $nameTest")
    }

    fun getMeas(): Array<String> {
        val readResponse = readRequest("MEAS?")
        try {
            val values = readResponse.split(',')
            val voltage = values[2].replace("kV", "").replace(",", ".")
            voltage.toDouble()
            val value = values[3].replace(" mA", "").replace(" ohm", "").replace("M", "").replace(",", ".")
            value.toDouble()
            val result = with(values[1]) {
                when {
                    contains("PASS") -> "0"
                    contains("FAIL") -> "1"
                    contains("VIEW") -> "2"
                    contains("TEST") -> "3"
                    contains("ERROR") -> "4"
                    else -> throw Exception()
                }
            }
            val time = if (!values[4].contains("R=")){
                values[4].replace("T=", "").replace("S","")
            } else {
                "0"
            }
//
            println("READ_RESPONSE(Valid) = $readResponse")
            return arrayOf(voltage, value, time, result)
        }
        catch (e: Exception) {
            println("READ_RESPONSE(Invalid) = $readResponse")
            println(e)
//            throw TransportException("Невалидная схема ответа")
            return arrayOf("0", "0", "0", "0")
        }
    }

    override fun readRegister(register: DeviceRegister) {
        isResponding = try {
            transactionWithAttempts {
                register.value = getMeas()[register.address.toInt()].toDouble()
            }
            true
        } catch (e: TransportException) {
            false
        }
    }
}
