package ru.avem.kspem.controllers

import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import javafx.scene.text.Text
import ru.avem.kspem.communication.model.CommunicationModel
import ru.avem.kspem.communication.model.devices.avem.avem7.Avem
import ru.avem.kspem.communication.model.devices.cs02021.CS02021
import ru.avem.kspem.communication.model.devices.delta.Delta
import ru.avem.kspem.communication.model.devices.owen.pr.OwenPr
import ru.avem.kspem.communication.model.devices.owen.pr.OwenPrModel
import ru.avem.kspem.communication.model.devices.avem.phaseMeter.PhaseMeter
import ru.avem.kspem.communication.model.devices.tilkom.T42
import ru.avem.kspem.communication.model.devices.trm202.TRM202
import ru.avem.kspem.data.*
import ru.avem.kspem.utils.LogTag
import ru.avem.kspem.utils.sleep
import ru.avem.kspem.view.ExpView
import ru.avem.stand.modules.r.communication.model.devices.avem.ikas.IKAS8
import tornadofx.*
import tornadofx.controlsfx.infoNotification
import java.io.File
import java.text.SimpleDateFormat
import kotlin.concurrent.thread
import kotlin.experimental.and

abstract class CustomController() : Component(), ScopedInstance {
    val pr102 = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.DD2_1) as OwenPr
    val avemU = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.PV21) as Avem
    val avemDpr = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.PV22) as Avem
    val ikas = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.PR61) as IKAS8
    val trm202 = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.PS81) as TRM202
    val t42 = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.T42) as T42
    val cs02 = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.PRV89) as CS02021
    val delta = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.UZ91) as Delta
    val phaseMeter = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.OGS_1) as PhaseMeter
    val cm = CommunicationModel

    val controller: MainViewController by inject()
    abstract val name: String
    abstract val model: View
    val view = find<ExpView>()
    var isStartPressed = false
    var isStopPressed = false
    var door = false
    var ekran = false
    var km1ctrl = false
    var km5ctrl = false
    var km6ctrl = false
    var km7ctrl = false

    @Volatile
    var isExperimentRunning = false

    @Volatile
    protected var cause: String = ""
        set(value) {
            if (value.isNotEmpty()) {
                isExperimentRunning = false
                appendMessageToLog(LogTag.ERROR, "???????????????????? ??????????????????")
                if (!field.contains(value)) field += "${if (field != "") "/" else ""}$value"
            } else {
                field = value
            }
        }

    open fun appendMessageToLog(tag: LogTag, _msg: String) {
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
            view.vBoxLog.add(msg)
            File("cfg\\log.txt").appendText("\n${SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis())} | $_msg")
        }
    }

    open fun start() {
        isExperimentRunning = true
        cause = ""
        disableButtons()

        if(isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "?????????????????????????? ????102...")
            initPR()
        }
    }

    open fun stop() {}

    fun disableButtons() {
        runLater {
            view.btnExit.isDisable = true
            view.btnNext.isDisable = true
            view.btnStart.isDisable = true
            view.btnStop.isDisable = false
        }
    }

    fun enableButtons() {
        runLater {
            view.btnExit.isDisable = false
            view.btnNext.isDisable = false
            view.btnStart.isDisable = false
            view.btnStop.isDisable = true
        }
    }


    fun initPR() {
        isStartPressed = false
        isStopPressed = false
        if (!pr102.isResponding) {
            cause = "????102 ???? ????????????????"
        } else {
            cm.addWritingRegister(
                CommunicationModel.DeviceID.DD2_1,
                OwenPrModel.RESET_DOG,
                1.toShort()
            )
            pr102.initOwenPR()
            sleep(1000)
            cm.startPoll(CommunicationModel.DeviceID.DD2_1, OwenPrModel.INPUTS_REGISTER) { value ->
                km1ctrl = value.toShort() and 1 > 0              // 1
                km5ctrl = value.toShort() and 2 > 0              // 2
                km6ctrl = value.toShort() and 4 > 0              // 3
                km7ctrl = value.toShort() and 8 > 0              // 4
                isStopPressed = value.toShort() and 16 > 0       // 5
                isStartPressed = value.toShort() and 32 > 0      // 6
                door = value.toShort() and 64 > 0                // 7
                ekran = value.toShort() and 128 > 0              // 8
            }
            sleep(1000)
            thread(isDaemon = true) {
                while (isExperimentRunning) {
                    if (!isMGR) {
                        if (!pr102.isResponding) cause = "???????????????? ?????????? ?? ????200"
                        if (isStopPressed) cause = "???????????? ???????????? <????????>"
                        if (door) cause = "?????????????? ?????????? ??????????"
                        if (ekran) cause = "???????????? ??????????"
                    }
                    sleep(100)
                }
            }
        }
        checkKMStat()
    }

    fun checkKMStat() {
        if (km1ctrl) cause = "????1 ???? ??????????????????"
        if (km5ctrl) cause = "????5 ???? ??????????????????"
        if (km6ctrl) cause = "????6 ???? ??????????????????"
        if (km7ctrl) cause = "????7 ???? ??????????????????"
    }

    fun initButtonPost() {
        appendMessageToLog(LogTag.DEBUG, "?????????????????????????? ???????????????????? ??????????")
        var timer = 300
        runLater {
            infoNotification("????????????????", "?????????????? <????????> ???? ?????????????????? ??????????", Pos.CENTER)
        }
        while (!isStartPressed) {
            sleep(100)
            timer--
            if (isStartPressed || !isExperimentRunning) break
            if (timer <= 0)
                cause = "???? ???????????? ???????????? <????????>"
        }
    }

    fun finalizeExperiment() {
        isExperimentRunning = false
        cm.clearPollingRegisters()
        pr102.resetKMS()
        enableButtons()
    }

    fun loadExpModel() {
        view.vboxExp.children.clear()
        view.vboxExp.children.add(model.root)
    }
}