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
        super.start()


        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация Т42...")
            t42.checkResponsibility()
            if (!t42.isResponding) {
//                appendMessageToLog(LogTag.ERROR, "Датчик момента не отвечает")
                cause = "Т42 не отвечает"
//                moment = 0.5
//                model.data.m.value = "0.5"
//                model.data.n.value = "60"
            } else {
                cm.startPoll(CommunicationModel.DeviceID.M42, T42Model.TORQUE) { value ->
                    if (!t42.isResponding) cause = "T42 не отвечает"
                    moment = value.toDouble()
                    model.data.m.value = value.autoformat()
                }
                cm.startPoll(CommunicationModel.DeviceID.M42, T42Model.RPM) { value ->
                    model.data.n.value = value.autoformat()
                }
            }
        }

        if (isExperimentRunning) {
            pr102.km1(true)
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
            var timer = 10.0
            while (isExperimentRunning && timer > 0) {
                sleep(100)
                timer -= 0.1
            }
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
            try {
                model.data.mDeviation.value =
                    abs(((model.data.mMax.value.toDouble() - model.data.mAvg.value.toDouble()) / model.data.mAvg.value.toDouble() * 100)).autoformat()
                protocolModel.momentDeviation = model.data.mDeviation.value
            } catch (e:Exception) {
                appendMessageToLog(LogTag.ERROR, "Не удалось рассчитать разброс")
            }
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

        when (cause) {
            "" -> {
                if (model.data.mDeviation.value.toDoubleOrNull() != null) {
                    if (model.data.mDeviation.value.toDouble() > 20.0) {
                        appendMessageToLog(LogTag.ERROR, "Разброс более 20%")
                        model.data.result.value = "Не соответствует"
                    } else {
                        model.data.result.value = "Успешно"
                        appendMessageToLog(LogTag.MESSAGE, "Испытание завершено успешно")
                    }
                } else {
                    appendMessageToLog(LogTag.ERROR, "Ошибка рассчета отклонения")
                    model.data.result.value = "Прервано"
                }
//                controller.next()
            }
            else -> {
                model.data.result.value = "Прервано"
                appendMessageToLog(LogTag.ERROR, "Испытание прервано по причине: $cause")
//                cause = "момент"
            }
        }
        finalizeExperiment()
        protocolModel.momentResult = model.data.result.value
        restoreData()
    }

    private fun checkMoment() {
        var avgM = moment
        var maxM = 0.0
        var avgSum = 0.0
        var avgCounter = 0
        val timer = System.currentTimeMillis()
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