package ru.avem.kspem.controllers.expControllers

import ru.avem.kspem.communication.model.CommunicationModel
import ru.avem.kspem.communication.model.devices.avem.avem7.AvemModel
import ru.avem.kspem.communication.model.devices.avem.phaseMeter.PhaseMeterModel
import ru.avem.kspem.communication.model.devices.delta.Delta
import ru.avem.kspem.communication.model.devices.delta.DeltaModel
import ru.avem.kspem.communication.model.devices.tilkom.T42Model
import ru.avem.kspem.controllers.CustomController
import ru.avem.kspem.data.objectModel
import ru.avem.kspem.data.protocolModel
import ru.avem.kspem.utils.LogTag
import ru.avem.kspem.utils.Singleton
import ru.avem.kspem.utils.sleep
import ru.avem.kspem.view.expViews.DprView
import ru.avem.stand.utils.autoformat
import ru.avem.stand.utils.toDoubleOrDefault
import java.util.*
import kotlin.math.abs
import kotlin.math.round


class DprController : CustomController() {
    override val model: DprView by inject()
    override val name = model.name
    var deltaStatus = 0
    var rpm = 0.0
    var volt = 0.0
    var voltage = 0.0
    var ptp = 0.0
    var amp = 0.0
    var impulse = 0.0
    var period = 0.0

    override fun start() {
        model.clearTables()
        super.start()

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация Т42...")
            t42.checkResponsibility()
            if (!t42.isResponding) {
                cause = "Т42 не отвечает"
            } else {
                cm.startPoll(CommunicationModel.DeviceID.T42, T42Model.RPM) { value ->
                    if (!t42.isResponding) cause = "T42 не отвечает"
                    rpm = value.toDouble()
                }
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация АВЭМ-4...")
            avemDpr.checkResponsibility()
            if (!avemDpr.isResponding) {
                cause = "АВЭМ-4 не отвечает"
            } else {
                cm.startPoll(CommunicationModel.DeviceID.PV22, AvemModel.AMP) { value ->
                    if (!avemDpr.isResponding) cause = "АВЭМ-4 не отвечает"
                    amp = value.toDouble()
                }
                cm.startPoll(CommunicationModel.DeviceID.PV22, AvemModel.PTP) { value ->
                    ptp = value.toDouble()
                }
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация ОГС-1...")
            phaseMeter.checkResponsibility()
            if (!phaseMeter.isResponding) {
                cause = "ОГС-1 не отвечает"
            } else {
                cm.startPoll(CommunicationModel.DeviceID.OGS_1, PhaseMeterModel.ulImpulsTime0) { value ->
                    impulse = value.toDouble()
//                    model.data.deviationMax.value = impulse.autoformat()
                }
                cm.startPoll(CommunicationModel.DeviceID.OGS_1, PhaseMeterModel.ulPeriodTime0) { value ->
                    period = value.toDouble()
//                    model.data.deviationMin.value = period.autoformat()
                }
                phaseMeter.setTime(1000)
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
            delta.setObjectParamsRun("%.2f".format(Locale.ENGLISH, 1.05*8 / objectModel!!.dprPairs.toDoubleOrDefault(8.0)).toDouble())
        }

        if (isExperimentRunning) {
            pr102.km6(true)
        }

        if (isExperimentRunning) {
            delta.startObject(Delta.Direction.REVERSE)
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
            if (rpm < 40) cause = "несоответствие скорости вращения"
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Измерение показаний каналов")
            pr102.km2_1(true)
            sleep(2000)
            model.data.ampSinP.value = (ptp/2.0).autoformat()
//            model.data.maxSinP.value = impulse.autoformat()
//            model.data.minSinP.value = period.autoformat()
            model.data.maxSinP.value = amp.autoformat()
            model.data.minSinP.value = (amp-ptp).autoformat()
            model.data.dprSinP.value = (impulse/period*360.0).autoformat()
            pr102.km2_1(false)
        }

        if (isExperimentRunning) {
            pr102.km2_2(true)
            sleep(2000)
            model.data.ampCosP.value = (ptp/2.0).autoformat()
//            model.data.maxCosP.value = impulse.autoformat()
//            model.data.minCosP.value = period.autoformat()
            model.data.maxCosP.value = amp.autoformat()
            model.data.minCosP.value = (amp-ptp).autoformat()
            model.data.dprCosP.value = (impulse/period*360.0).autoformat()
            pr102.km2_2(false)

        }

        if (isExperimentRunning) {
            pr102.km2_3(true)
            sleep(2000)
            model.data.ampSinN.value = (ptp/2.0).autoformat()
//            model.data.maxSinN.value = impulse.autoformat()
//            model.data.minSinN.value = period.autoformat()
            model.data.maxSinN.value = amp.autoformat()
            model.data.minSinN.value = (amp-ptp).autoformat()
            model.data.dprSinN.value = (impulse/period*360.0).autoformat()
            try {
                model.data.hz.value = round(((600000.0 / rpm) / period)).autoformat()
            } catch (e:Exception) {
                println("DPR EXCEPTION ${e.stackTrace}")
            }
            pr102.km2_3(false)
        }

        if (isExperimentRunning) {
            pr102.km2_4(true)
            sleep(2000)
            model.data.ampCosN.value = (ptp/2.0).autoformat()
//            model.data.maxCosN.value = impulse.autoformat()
//            model.data.minCosN.value = period.autoformat()
            model.data.maxCosN.value = amp.autoformat()
            model.data.minCosN.value = (amp-ptp).autoformat()
            model.data.dprCosN.value = (impulse/period*360.0).autoformat()
            pr102.km2_4(false)
        }

        if (isExperimentRunning) {
            calcDeviation()
        }


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
                if (model.data.deviationMax.value.toDoubleOrNull() != null && model.data.deviationMin.value.toDoubleOrNull() != null ) {
                    when {
                        model.data.deviationMax.value.toDouble() > objectModel!!.dprDev.toDoubleOrDefault(5.0) -> {
                            appendMessageToLog(LogTag.ERROR, "Разброс Umax более ${objectModel!!.dprDev.toDoubleOrDefault(5.0)}%")
                            model.data.result.value = "Не соответствует"
                        }
                        model.data.deviationMin.value.toDouble() > objectModel!!.dprDev.toDoubleOrDefault(5.0) -> {
                            appendMessageToLog(LogTag.ERROR, "Разброс Umin более ${objectModel!!.dprDev.toDoubleOrDefault(5.0)}%")
                            model.data.result.value = "Не соответствует"
                        }
                        model.data.hz.value.toDouble() > objectModel!!.dprPairs.toDoubleOrDefault(8.0) -> {
                            appendMessageToLog(LogTag.ERROR, "Несоответствие числу пар полюсов двигателя: ${objectModel!!.dprPairs.toDoubleOrDefault(8.0)}")
                            model.data.result.value = "Не соответствует"
                        }
                        else -> {
                            model.data.result.value = "Успешно"
                            appendMessageToLog(LogTag.MESSAGE, "Испытание завершено успешно")
                        }
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
        saveData()
        if (model.data.result.value == "Успешно" && Singleton.isAutoMod) {
            controller.next()
        }
    }

    override fun stop() {
        cause = "Отменено оператором"
        model.data.result.value = "Прервано"
    }

    private fun calcDeviation() {
        val listMax = listOf(
            model.data.maxCosN.value.toDoubleOrDefault(0.0),
            model.data.maxSinN.value.toDoubleOrDefault(0.0),
            model.data.maxCosP.value.toDoubleOrDefault(0.0),
            model.data.maxSinP.value.toDoubleOrDefault(0.0)
        )
        val min1 = listMax.minOrNull() ?: 0.0
        val max1 = listMax.maxOrNull() ?: 0.0
        if (min1 != 0.0 && max1 != 0.0) {
            model.data.deviationMax.value = "%.2f".format(Locale.ENGLISH, abs((max1 - min1) / min1 * 100))
        }

        val listMin = listOf(
            model.data.minCosN.value.toDoubleOrDefault(0.0),
            model.data.minSinN.value.toDoubleOrDefault(0.0),
            model.data.minCosP.value.toDoubleOrDefault(0.0),
            model.data.minSinP.value.toDoubleOrDefault(0.0)
        )
        val min2 = listMin.minOrNull() ?: 0.0
        val max2 = listMin.maxOrNull() ?: 0.0
        if (min2 != 0.0 && max2 != 0.0) {
            model.data.deviationMin.value = "%.2f".format(Locale.ENGLISH, abs((max2 - min2) / min2 * 100))
        }
    }

    private fun saveData() {
        protocolModel.dprampSinP = model.data.ampSinP.value
        protocolModel.dprampSinN = model.data.ampSinN.value
        protocolModel.dprampCosP = model.data.ampCosP.value
        protocolModel.dprampCosN = model.data.ampCosN.value
        protocolModel.dprmaxSinP = model.data.maxSinP.value
        protocolModel.dprmaxSinN = model.data.maxSinN.value
        protocolModel.dprmaxCosP = model.data.maxCosP.value
        protocolModel.dprmaxCosN = model.data.maxCosN.value
        protocolModel.dprminSinP = model.data.minSinP.value
        protocolModel.dprminSinN = model.data.minSinN.value
        protocolModel.dprminCosP = model.data.minCosP.value
        protocolModel.dprminCosN = model.data.minCosN.value
        protocolModel.dprdprSinP = model.data.minSinP.value
        protocolModel.dprdprSinN = model.data.minSinN.value
        protocolModel.dprdprCosP = model.data.minCosP.value
        protocolModel.dprdprCosN = model.data.minCosN.value
        protocolModel.dprdeviationMin = model.data.deviationMin.value
        protocolModel.dprdeviationMax = model.data.deviationMax.value
        protocolModel.dprdeviationDpr = model.data.deviationDpr.value
        protocolModel.dprhz = model.data.hz.value
        protocolModel.dprresult = model.data.result.value
    }
}