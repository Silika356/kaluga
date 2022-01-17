package ru.avem.kspem.controllers.expControllers

import javafx.scene.chart.XYChart
import ru.avem.kspem.communication.model.CommunicationModel
import ru.avem.kspem.communication.model.devices.delta.DeltaModel
import ru.avem.kspem.communication.model.devices.tilkom.T42Model
import ru.avem.kspem.controllers.CustomController
import ru.avem.kspem.data.protocolModel
import ru.avem.kspem.utils.LogTag
import ru.avem.kspem.utils.sleep
import ru.avem.kspem.view.expViews.RUNNINGView
import ru.avem.stand.utils.autoformat
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


        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация Т42...")
            t42.checkResponsibility()
            if (!trm202.isResponding) {
                cause = "Т42 не отвечает"
            } else {
                cm.startPoll(CommunicationModel.DeviceID.M42, T42Model.TORQUE) { value ->
                    moment = value.toDouble()
                    model.data.m.value = value.autoformat()
                }
                cm.startPoll(CommunicationModel.DeviceID.M42, T42Model.RPM) { value ->
                    model.data.n.value = value.autoformat()
                }
            }
        }

        if (isExperimentRunning) {
            pr200.km1(true)
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация Delta...")
            var timeDelta = 100
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
            delta.setObjectParamsRun(1)
        }

        if (isExperimentRunning) {
            delta.startObject()
            appendMessageToLog(LogTag.DEBUG, "Разгон ПЧ")
        }

        if (isExperimentRunning) {
            startChart()
        }

        if (isExperimentRunning) {
            var timer = 10.0
            while (isExperimentRunning && timer > 0) {
                sleep(100)
                timer -= 0.1
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Анализ момента на валу")
            checkMoment()
            protocolModel.momentAVG = model.data.mAvg.value
            protocolModel.momentMAX = model.data.mMax.value
            protocolModel.momentN = model.data.n.value
            try {
                protocolModel.momentDeviation =
                    abs(((model.data.mMax.value.toDouble() - model.data.mAvg.value.toDouble()) / model.data.mAvg.value.toDouble() * 100)).autoformat()
            } catch (e:Exception) {}
        }


        delta.stopObject()

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Выключение ПЧ")
        }
        var timerDelta = 5.0
        while (timerDelta > 0) {
            sleep(100)
            timerDelta -= 0.1
        }

        isChart = false
        cm.stopPoll(CommunicationModel.DeviceID.M42)
        cm.stopPoll(CommunicationModel.DeviceID.UZ91)

        when (cause) {
            "" -> {
                model.data.result.value = "Успешно"
                appendMessageToLog(LogTag.MESSAGE, "Испытание завершено успешно")
                controller.next()
            }
            else -> {
                model.data.result.value = "Прервано"
                appendMessageToLog(LogTag.ERROR, "Испытание прервано по причине: $cause")
                cause = "момент"
                enableButtons()
            }
        }
        protocolModel.momentResult = model.data.result.value
        restoreData()
    }

    private fun checkMoment() {
        var avgM = moment
        var maxM = 0.0
        var avgSum = 0.0
        var avgCounter = 0
        val timer = System.currentTimeMillis()
        thread(isDaemon = true) {
            while (System.currentTimeMillis() - timer < 10000 && isExperimentRunning) {
                sleep(10)
                avgSum += moment
                avgCounter++
                if (moment > maxM) maxM = moment
            }
            avgM = avgSum / avgCounter
            model.data.mAvg.value = avgM.autoformat()
            model.data.mMax.value = maxM.autoformat()
        }
    }

    private fun startChart() {
        var curTime = 0.1
        thread(isDaemon = true) {
            runLater {
                model.series.data.clear()
            }
            sleep(100)
            while (isChart) {
                runLater {
                    model.series.data.add(XYChart.Data(curTime, moment))
                }
                sleep(10)
                curTime += 0.01
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