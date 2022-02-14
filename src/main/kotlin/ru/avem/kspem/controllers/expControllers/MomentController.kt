package ru.avem.kspem.controllers.expControllers

import javafx.scene.chart.XYChart
import ru.avem.kspem.communication.model.CommunicationModel
import ru.avem.kspem.communication.model.devices.delta.DeltaModel
import ru.avem.kspem.communication.model.devices.tilkom.T42Model
import ru.avem.kspem.controllers.CustomController
import ru.avem.kspem.data.objectModel
import ru.avem.kspem.data.protocolModel
import ru.avem.kspem.utils.LogTag
import ru.avem.kspem.utils.Singleton
import ru.avem.kspem.utils.sleep
import ru.avem.kspem.view.expViews.RUNNINGView
import ru.avem.stand.utils.autoformat
import ru.avem.stand.utils.toDoubleOrDefault
import tornadofx.runLater
import kotlin.concurrent.thread
import kotlin.math.abs


class MomentController : CustomController() {
    override val model: RUNNINGView by inject()
    override val name = model.name
    var deltaStatus = 0
    var isChart = true
    var moment = 0.0

    override fun start() {
        model.clearTables()
        super.start()


        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация Т42...")
            t42.checkResponsibility()
            if (!t42.isResponding) {
                cause = "Т42 не отвечает"
            } else {
                cm.startPoll(CommunicationModel.DeviceID.T42, T42Model.TORQUE) { value ->
                    if (!t42.isResponding) cause = "T42 не отвечает"
                    moment = abs(value.toDouble())
                    model.data.m.value = moment.autoformat()
                }
                cm.startPoll(CommunicationModel.DeviceID.T42, T42Model.RPM) { value ->
                    model.data.n.value = value.autoformat()
                }
            }
        }

        if (isExperimentRunning) {
            pr102.km1(true)
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация Delta...")
            var timeDelta = 50
            while (isExperimentRunning && timeDelta-- > 0) {
                sleep(100)
            }

            val timer = System.currentTimeMillis()
            while (isExperimentRunning && !delta.isResponding) {
                delta.checkResponsibility()
                sleep(100)
                if ((System.currentTimeMillis() - timer) > 30000) cause = "Delta не отвечает"
            }

            cm.startPoll(CommunicationModel.DeviceID.UZ91, DeltaModel.STATUS_REGISTER) { value ->
                deltaStatus = value.toInt()
                if (!delta.isResponding && isExperimentRunning) cause = "Delta не отвечает"
            }
        }

        if (isExperimentRunning) {
            delta.setObjectParamsRun(1.05)
        }

        if (isExperimentRunning) {
            delta.startObject()
            appendMessageToLog(LogTag.DEBUG, "Разгон ПЧ")
        }


        if (isExperimentRunning) {
            var timer = 5.0
            while (isExperimentRunning && timer > 0) {
                sleep(100)
                timer -= 0.1
            }
        }

        if (isExperimentRunning) {
            t42.readRegister(t42.getRegisterById(T42Model.RPM))
            if (t42.getRegisterById(T42Model.RPM).value.toDouble() < 40) cause = "несоответствие скорости вращения"
        }

        if (isExperimentRunning) {
            startChart()
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Анализ момента на валу")
            checkMoment()
            protocolModel.momentAVG = model.data.mAvg.value
            protocolModel.momentMAX = model.data.mMax.value
            protocolModel.momentN = model.data.n.value
        }


        isChart = false

//        while (isExperimentRunning) {
//            sleep(100)
//        }

        delta.stopObject()

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Выключение ПЧ")
        }
        var timerDelta = 5.0
        while (timerDelta > 0) {
            sleep(100)
            timerDelta -= 0.1
        }


        when (cause) {
            "" -> {
                if (model.data.mMax.value.toDouble() > objectModel!!.momentMax.toDoubleOrDefault(1000.0)) {
                    appendMessageToLog(LogTag.ERROR, "Макс. момент более ${objectModel!!.momentMax.toDoubleOrDefault(1000.0)} мН*м")
                    model.data.result.value = "Не соответствует"
                } else {
                    model.data.result.value = "Успешно"
                    appendMessageToLog(LogTag.MESSAGE, "Испытание завершено успешно")
                }
            }
//                controller.next()
            else -> {
                model.data.result.value = "Прервано"
                appendMessageToLog(LogTag.ERROR, "Испытание прервано по причине: $cause")
            }
        }
        finalizeExperiment()
        protocolModel.momentResult = model.data.result.value
        restoreData()
        if (model.data.result.value == "Успешно" && Singleton.isAutoMod) {
            controller.next()
        }
    }

    private fun checkMoment() {
        var avgM = 0.0
        var maxM = 0.0
        var avgSum = 0.0
        var avgCounter = 0
        val timer = System.currentTimeMillis()
        while (System.currentTimeMillis() - timer < 10000 && isExperimentRunning) {
            sleep(50)
            avgSum += moment
            avgCounter++
            if (moment > maxM) maxM = moment
        }
        avgM = avgSum / avgCounter
        model.data.mAvg.value = avgM.autoformat()
        model.data.mMax.value = maxM.autoformat()
    }

    private fun startChart() {
        isChart = true
        var curTime = 0.0
        thread(isDaemon = true) {
            runLater {
                model.series.data.clear()
            }
            sleep(100)
            while (isChart) {
                runLater {
                    model.series.data.add(XYChart.Data(curTime, moment))
                }
                curTime += 0.05
                sleep(50)
            }
        }
    }

    override fun stop() {
        cause = "Отменено оператором"
        model.data.result.value = "Прервано"
    }

    private fun saveData() {
        protocolModel.momentAVG = ""
        protocolModel.momentMAX = ""
        protocolModel.momentN = ""
        protocolModel.momentDeviation = ""
        protocolModel.momentResult = ""
    }

    private fun restoreData() {
        model.data.n.value = protocolModel.momentN
    }
}