package ru.avem.kspem.controllers

import com.fazecast.jSerialComm.SerialPort
import javafx.application.Platform
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import javafx.scene.text.Text
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.kspem.app.Main.Companion.isAppRunning
import ru.avem.kspem.communication.model.CommunicationModel
import ru.avem.kspem.communication.model.devices.avem.avem7.Avem
import ru.avem.kspem.communication.model.devices.delta.Delta
import ru.avem.kspem.communication.model.devices.owen.pr.OwenPr
import ru.avem.kspem.data.*
import ru.avem.kspem.database.entities.Protocol
import ru.avem.kspem.utils.*
import ru.avem.kspem.view.ExpView
import ru.avem.kspem.view.MainView
import tornadofx.*
import tornadofx.controlsfx.infoNotification
import java.io.File
import java.text.SimpleDateFormat
import kotlin.concurrent.thread


class MainViewController : Controller() {
    private val pr102 = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.DD2_1) as OwenPr
    private val avem4 = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.PV21) as Avem
    val delta = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.UZ91) as Delta

    private val expView: ExpView by inject()
    private val mainView: MainView by inject()
    var position1 = ""
    var serialNum = ""
    var objectName = ""
    private var logBuffer: String? = null
    var expListRaw = mutableListOf<CustomController>(mgr,ikas,moment, voltage)
    var expList = expListRaw.iterator()


    lateinit var currentExp: CustomController

    @Volatile
    var isExperimentRunning: Boolean = false

    @Volatile
    var isDevicesResponding = false

    private var cause: String = ""
        set(value) {
            if (value != "") {
                isExperimentRunning = false
//                view.buttonStart.isDisable = true
            }
            field = value
        }


    fun showAboutUs() {
        runLater {
            Toast.makeText("Версия ПО: 1.0.0\nВерсия БСУ: 1.0.0\nДата: 25.11.2021").show(Toast.ToastType.INFORMATION)
        }
    }

    private fun appendOneMessageToLog(tag: LogTag, message: String) {
        if (logBuffer == null || logBuffer != message) {
            logBuffer = message
            appendMessageToLog(tag, message)
        }
    }

    fun appendMessageToLog(tag: LogTag, _msg: String) {
        val msg = Text("${SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis())} | $_msg")
        msg.style {
            fill = when (tag) {
                LogTag.MESSAGE -> tag.c
                LogTag.ERROR -> tag.c
                LogTag.DEBUG -> tag.c
            }
            stroke = Color.BLACK
            effect = DropShadow()
        }

        Platform.runLater {
            expView.vBoxLog.add(msg)
            File("cfg\\log.txt").appendText("\n${SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis())} | $_msg")
        }
    }

//    fun start() {
//
//    }

//    fun repeat() {
//        expList = expListRaw.iterator()
//        currentExp = expList.next()
//        loadExp()
//    }

    fun next() {
        if (expList.hasNext()) {
            currentExp = expList.next()
            loadExp()
        } else {
            saveProtocol()
            clearProtocol()
            find<ExpView>().replaceWith<MainView>()
        }
    }

    fun initExp() {
        expList = expListRaw.iterator()
        currentExp = expList.next()
        loadExp()
    }

    fun loadExp() {
        expView.vBoxLog.clear()
        currentExp.loadExpModel()
    }

    fun startExperiment() {
        thread(isDaemon = true) {
            currentExp.start()
        }
    }
    fun exit() {
        showTwoWayDialog(title = "Внимание!",
            text = "Текущий протокол будет сохранен и очищен",
            way1Title = "Подтвердить",
            way2Title = "Отменить",
            way1 = {
                saveProtocol()
                clearProtocol()
                find<ExpView>().replaceWith<MainView>()
            },
            way2 = {
            },
            currentWindow = primaryStage.scene.window)
                find<ExpView>().replaceWith<MainView>()
    }

    fun stopExperiment() {
        currentExp.stop()
    }

//    private fun getNotRespondingDevicesString(): String {
//        return String.format(
//            "%s %s%s%s%s",
//            "Испытание прервано по причине: \nпотеряна связь с устройствами:",
//            if (pr102.isResponding) "" else "\nОвен ПР102 ",
//            if (pm130.isResponding) "" else "\nPM130 ",
//            if (latr.isResponding) "" else "\nАРН ",
//            if (avem.isResponding) "" else "\nАВЭМ7 "
//        )
//    }

//    fun causeSet(cause: String) {
//        this.cause = cause
//        if (cause.isNotEmpty()) {
//            isExperimentRunning = false
//            appendOneMessageToLog(LogTag.ERROR, "Отмена испытания")
//        }
//    }

    //    private fun finalizeExperiment() {
//        CommunicationModel.clearPollingRegisters()
////        pr102.resetKMS()
//        isExperimentRunning = false
//        runLater {
//            expView.btnStart.isDisable = false
//            expView.btnExit.isDisable = false
//        }
//    }

    private fun saveProtocol() {
        transaction {
            Protocol.new {
                //DATA
                objectName = protocolModel.objectName
                date = protocolModel.date
                time = protocolModel.time
                dataType = protocolModel.dataType
                operator = protocolModel.operator
                serial = protocolModel.serial
                dataP = protocolModel.dataP
                dataU = protocolModel.dataU
                dataN = protocolModel.dataN
                dataF = protocolModel.dataF
                dataKPD = protocolModel.dataKPD
                dataCOS = protocolModel.dataCOS
                //MGR//
                mgrU = protocolModel.mgrU
                mgrR15 = protocolModel.mgrR15
                mgrR60 = protocolModel.mgrR60
                mgrkABS = protocolModel.mgrkABS
                mgrResult = protocolModel.mgrResult
                //IKAS//
                ikasRA = protocolModel.ikasRA
                ikasRB = protocolModel.ikasRB
                ikasRC = protocolModel.ikasRC
                ikasDeviation = protocolModel.ikasDeviation
                ikasResult = protocolModel.ikasResult
                //MOMENT//
                momentN = protocolModel.momentN
                momentAVG = protocolModel.momentAVG
                momentMAX = protocolModel.momentMAX
                momentDeviation = protocolModel.momentDeviation
                momentResult = protocolModel.momentResult
                //VOLTAGE//
                voltageUAB = protocolModel.voltageUAB
                voltageUBC = protocolModel.voltageUBC
                voltageUCA = protocolModel.voltageUCA
                voltageF = protocolModel.voltageF
                voltageN = protocolModel.voltageN
                voltageUAB1000 = protocolModel.voltageUAB1000
                voltageUBC1000 = protocolModel.voltageUBC1000
                voltageUCA1000 = protocolModel.voltageUCA1000
                voltageDeviation = protocolModel.voltageDeviation
                voltageResult = protocolModel.voltageResult
                //DPR//

            }
        }
        runLater {
            infoNotification("Сохранение протокола", "Протокол сохранен")
        }
    }

    init {
        thread(isDaemon = true) {
            while (isAppRunning) {
                sleep(2000)
                val serialPortBSY = SerialPort.getCommPorts().filter {
                    it.toString() == "CP2103 USB to RS-485"
                }
                if (serialPortBSY.isEmpty()) {
                    runLater {
                        mainView.comIndicate.fill = State.BAD.c
                        mainView.circlePR200.fill = State.INTERMEDIATE.c
                        expView.circleAVEM.fill = State.INTERMEDIATE.c
                        expView.circlePR200.fill = State.INTERMEDIATE.c
                    }
                } else {
                    runLater {
                        mainView.comIndicate.fill = State.OK.c
                    }
                    pr102.checkResponsibility()
                    if (pr102.isResponding) {
                        runLater {
                            mainView.circlePR200.fill = State.OK.c
                            expView.circlePR200.fill = State.OK.c
                        }
                    } else {
//                        pr102.resetKMS()
                        runLater {
                            mainView.circlePR200.fill = State.BAD.c
                            expView.circlePR200.fill = State.BAD.c
                        }
                    }
                    avem4.checkResponsibility()
                    if (avem4.isResponding) {
                        runLater {
                            expView.circleAVEM.fill = State.OK.c
                        }
                    } else {
                        runLater {
                            expView.circleAVEM.fill = State.BAD.c
                        }
                    }
                    delta.checkResponsibility()
                    if (delta.isResponding) {
                        runLater {
                            expView.circleDelta.fill = State.OK.c
                        }
                    } else {
                        runLater {
                            expView.circleDelta.fill = State.BAD.c
                        }
                    }
                }
//                val serialPortGPT = SerialPort.getCommPorts().filter {
//                    it.toString() == "CP2103 USB to GPT"
//                }
//                if (serialPortGPT.isEmpty()) {
//                    runLater {
//                        mainView.gptIndicate.fill = State.BAD.c
//                    }
//                } else {
//                    runLater {
//                        mainView.gptIndicate.fill = State.OK.c
//                    }
//                }
//                val serialPortDelta = SerialPort.getCommPorts().filter {
//                    it.toString() == "CP2103 USB to DELTA"
//                }
//                if (serialPortDelta.isEmpty()) {
//                    runLater {
//                        mainView.deltaIndicate.fill = State.BAD.c
//                        expView.circleDelta.fill = State.INTERMEDIATE.c
//                    }
//                } else {
//                    runLater {
//                        mainView.deltaIndicate.fill = State.OK.c
//                    }
//                    delta.checkResponsibility()
//                    if (delta.isResponding) {
//                        runLater {
//                            expView.circleDelta.fill = State.OK.c
//                        }
//                    } else {
//                        runLater {
//                            expView.circleDelta.fill = State.BAD.c
//                        }
//                    }
//                }
            }
        }
    }

    fun clearProtocol() {
            protocolModel.objectName = ""
            protocolModel.date = ""
            protocolModel.time = ""
            protocolModel.operator = ""
            protocolModel.serial = ""
            protocolModel.dataType = ""
            protocolModel.dataP = ""
            protocolModel.dataU = ""
            protocolModel.dataI = ""
            protocolModel.dataN = ""
            protocolModel.dataF = ""
            protocolModel.dataKPD = ""
            protocolModel.dataCOS = ""
//MGR//
            protocolModel.mgrU = ""
            protocolModel.mgrR15 = ""
            protocolModel.mgrR60 = ""
            protocolModel.mgrkABS = ""
            protocolModel.mgrTemp = ""
            protocolModel.mgrResult = ""
//IKAS//
            protocolModel.ikasRA = ""
            protocolModel.ikasRB = ""
            protocolModel.ikasRC = ""
            protocolModel.ikasDeviation = ""
            protocolModel.ikasResult = ""
//MOMENT//
            protocolModel.momentN = ""
            protocolModel.momentAVG = ""
            protocolModel.momentMAX = ""
            protocolModel.momentDeviation = ""
            protocolModel.momentResult = ""
//VOLTAGE//
            protocolModel.voltageUAB = ""
            protocolModel.voltageUBC = ""
            protocolModel.voltageUCA = ""
            protocolModel.voltageN = ""
            protocolModel.voltageF = ""
            protocolModel.voltageUAB1000 = ""
            protocolModel.voltageUBC1000 = ""
            protocolModel.voltageUCA1000 = ""
            protocolModel.voltageDeviation = ""
            protocolModel.voltageResult = ""
    }
}
