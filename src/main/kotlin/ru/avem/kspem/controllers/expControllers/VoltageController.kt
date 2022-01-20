package ru.avem.kspem.controllers.expControllers

import javafx.scene.chart.XYChart
import ru.avem.kspem.communication.model.CommunicationModel
import ru.avem.kspem.communication.model.devices.avem.avem7.AvemModel
import ru.avem.kspem.communication.model.devices.delta.DeltaModel
import ru.avem.kspem.communication.model.devices.tilkom.T42Model
import ru.avem.kspem.controllers.CustomController
import ru.avem.kspem.data.objectModel
import ru.avem.kspem.data.protocolModel
import ru.avem.kspem.utils.LogTag
import ru.avem.kspem.utils.sleep
import ru.avem.kspem.view.expViews.VoltageView
import ru.avem.stand.utils.autoformat
import ru.avem.stand.utils.toDoubleOrDefault
import tornadofx.runLater
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.abs


class VoltageController : CustomController() {
    override val model: VoltageView by inject()
    override val name = model.name
    var deltaStatus = 0
    var isChart = true
    var rpm = 0.0
    var volt = 0.0
    var voltage = 0.0

    override fun start() {
        model.clearTables()
        super.start()

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация Т42...")
            t42.checkResponsibility()
            if (!t42.isResponding) {
//                appendMessageToLog(LogTag.ERROR,"Т42 не отвечает")
                cause = "Т42 не отвечает"
//                model.data.rpm.value = "500"
//                rpm = 500.0
            } else {
                cm.startPoll(CommunicationModel.DeviceID.M42, T42Model.RPM) { value ->
                    if (!t42.isResponding) cause = "T42 не отвечает"
                    model.data.rpm.value = value.autoformat()
                    rpm = value.toDouble()
                }
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация АВЭМ-4...")
            avemU.checkResponsibility()
            if (!avemU.isResponding) {
                cause = "АВЭМ-4 не отвечает"
            } else {
                cm.startPoll(CommunicationModel.DeviceID.PV21, AvemModel.VOLTAGE) { value ->
                    if (!avemU.isResponding) cause = "АВЭМ-4 не отвечает"
                    voltage = value.toDouble()
                }
                cm.startPoll(CommunicationModel.DeviceID.PV21, AvemModel.FREQUENCY) { value ->
                    model.data.freq.value = value.autoformat()
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
            delta.setObjectParamsRun("%.2f".format(Locale.ENGLISH, (objectModel!!.nNom.toDouble() / 3000.0 * 50.0)).toDouble())
        }

        if (isExperimentRunning) {
            pr102.km2(true)
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
            isChart = false
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Сохранение линейных напряжений")
            model.data.uAB.value = voltage.autoformat()
            model.data.uABcalc.value = (voltage * rpm / 1000).autoformat()

            protocolModel.voltageN = model.data.rpm.value
            protocolModel.voltageF = model.data.freq.value
            protocolModel.voltageUAB = model.data.uAB.value
            protocolModel.voltageUAB1000 = model.data.uABcalc.value
        }

        if (isExperimentRunning) {
            pr102.km2(false)
            pr102.km3(true)
            sleep(2000)
        }

        if (isExperimentRunning) {
            model.data.uBC.value = voltage.autoformat()
            model.data.uBCcalc.value = (voltage * rpm / 1000).autoformat()

            protocolModel.voltageUBC = model.data.uBC.value
            protocolModel.voltageUBC1000 = model.data.uBCcalc.value
        }

        if (isExperimentRunning) {
            pr102.km3(false)
            pr102.km4(true)
            sleep(2000)
        }

        if (isExperimentRunning) {
            model.data.uCA.value = voltage.autoformat()
            model.data.uCAcalc.value = (voltage * rpm / 1000).autoformat()

            protocolModel.voltageUCA = model.data.uCA.value
            protocolModel.voltageUCA1000 = model.data.uCAcalc.value

            val list = listOf(
                model.data.uAB.value.toDoubleOrDefault(0.0),
                model.data.uBC.value.toDoubleOrDefault(0.0),
                model.data.uCA.value.toDoubleOrDefault(0.0)
            )
            val min = list.min() ?: 0.0
            val max = list.max() ?: 0.0
            if (min != 0.0 && max != 0.0) {
                model.data.deviation.value = "%.4f".format(abs((max - min) / min * 100))
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


        isChart = false
        cm.stopPoll(CommunicationModel.DeviceID.M42)

        when (cause) {
            "" -> {
                if (model.data.deviation.value.toDoubleOrNull() != null) {
                    if (model.data.deviation.value.toDouble() > 20.0) {
                        appendMessageToLog(LogTag.ERROR, "Разброс более 20%")
                        model.data.result.value = "Не соответствует"
                    } else {
                        model.data.result.value = "Успешно"
                        appendMessageToLog(LogTag.MESSAGE, "Испытание завершено успешно")
                    }
                } else {
                    appendMessageToLog(LogTag.ERROR, "Не удалось рассчитать разброс")
                    model.data.result.value = "Прервано"
                }
            }
            else -> {
                model.data.result.value = "Прервано"
                appendMessageToLog(LogTag.ERROR, "Испытание прервано по причине: $cause")
//                cause = "момент"
//                enableButtons()
            }
        }
        finalizeExperiment()
        protocolModel.voltageResult = model.data.result.value
        model.data.rpm.value = protocolModel.voltageN
        model.data.freq.value = protocolModel.voltageF
    }

    private fun startChart() {
        var curTime = 0.1
        thread(isDaemon = true) {
            runLater {
                model.seriesAB.data.clear()
            }
            sleep(100)
            while (isChart) {
                runLater {
                    model.seriesAB.data.add(XYChart.Data(rpm, volt))
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
//        protocolModel.voltageN = model.data.N.value
        protocolModel.voltageUAB = model.data.uAB.value
        protocolModel.voltageUBC = model.data.uBC.value
        protocolModel.voltageUCA = model.data.uCA.value
        protocolModel.voltageUAB1000 = model.data.uABcalc.value
        protocolModel.voltageUBC1000 = model.data.uBCcalc.value
        protocolModel.voltageUCA1000 = model.data.uCAcalc.value
        protocolModel.voltageDeviation = model.data.deviation.value
        protocolModel.voltageResult = model.data.result.value
    }
}