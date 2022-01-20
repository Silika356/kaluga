package ru.avem.kspem.controllers.expControllers

import ru.avem.kspem.controllers.CustomController
import ru.avem.kspem.data.protocolModel
import ru.avem.kspem.utils.LogTag
import ru.avem.kspem.utils.sleep
import ru.avem.kspem.view.expViews.MGRView
import ru.avem.stand.utils.autoformat
import tornadofx.isDouble
import java.util.*


class MGRController : CustomController() {
    override val model: MGRView by inject()
    override val name = model.name

    override fun start() {
        model.clearTables()
        super.start()


        if (isExperimentRunning) {
            initButtonPost()
        }

        if (isExperimentRunning) {
            pr102.km7(true)
            pr102.km8(true)
        }
        if (isExperimentRunning) {
            sleep(1000)
            appendMessageToLog(LogTag.MESSAGE, "Инициализация Мегаомметра...")
            with(cs02) {
                var timer1 = 50
                while (timer1-- > 0) {
                    sleep(100)
                }
                checkResponsibility()

                if (isResponding) {
                    appendMessageToLog(LogTag.MESSAGE, "Измерение сопротивления 90 секунд")
                    setVoltage(100)
                    var timer = 90.0
                    while (isExperimentRunning && timer > 0) {
                        sleep(100)
                        model.data.time.value = "%.1f".format(Locale.ENGLISH, timer)
                        timer -= 0.1
                    }
                    if (isExperimentRunning) {
                        val mgrData = readData()
                        val measuredR60 = mgrData[0].toDouble()
                        val measuredUr = mgrData[1].toDouble()
                        val measuredAbs = mgrData[2].toDouble()
                        val measuredR15 = mgrData[3].toDouble()

                        val measuredR60Mohm = (measuredR60 / 1_000_000)
                        val measuredR15Mohm = (measuredR15 / 1_000_000)
                        if (measuredR60Mohm > 200_000) {
                            model.data.U.value = measuredUr.autoformat()
                            model.data.R15.value = "обрыв"
                            model.data.R60.value = "обрыв"
                            model.data.K_ABS.value = "обрыв"
                            cause = "обрыв"
                        } else {
                            model.data.U.value = measuredUr.autoformat()
                            model.data.R15.value = measuredR15Mohm.autoformat()
                            model.data.R60.value = measuredR60Mohm.autoformat()
                            model.data.K_ABS.value = measuredAbs.autoformat()
                            appendMessageToLog(LogTag.DEBUG, "Заземление")
                            timer = 30.0
                            while (isExperimentRunning && timer > 0) {
                                sleep(100)
                                model.data.time.value = "%.1f".format(Locale.ENGLISH, timer)
                                timer -= 0.1
                            }
                        }
                    }

                } else {
                    cause = "Меггер не отвечает"
                }
            }
        }
//        finalizeExperiment()
        pr102.km7(false)
        pr102.km8(false)


        when (cause) {
            "" -> {
                if (model.data.K_ABS.value.isDouble()) {
                    if (model.data.K_ABS.value.toDouble() < 1.3) {
                        appendMessageToLog(LogTag.ERROR, "Измеренный kABS < 1.3")
                        model.data.result.value = "Не соответствует"
//                        cause = "мегер"
                    } else {
                        model.data.result.value = "Соответствует"
                        appendMessageToLog(LogTag.MESSAGE, "Испытание завершено успешно")
//                        controller.next()
                    }
                } else {
                    model.data.result.value = "Обрыв"
                }
            }
            else -> {
                model.data.result.value = "Прервано"
                appendMessageToLog(LogTag.ERROR, "Испытание прервано по причине: $cause")
                enableButtons()
            }
        }
        finalizeExperiment()
        saveData()
    }


    override fun stop() {
        cause = "Отменено оператором"
        model.data.result.value = "Прервано"
    }

    private fun saveData() {
        protocolModel.mgrU = model.data.U.value
        protocolModel.mgrR15 = model.data.R15.value
        protocolModel.mgrR60 = model.data.R60.value
        protocolModel.mgrkABS = model.data.K_ABS.value
        protocolModel.mgrResult = model.data.result.value
    }
}