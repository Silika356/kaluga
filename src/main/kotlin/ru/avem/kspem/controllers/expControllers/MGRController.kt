package ru.avem.kspem.controllers.expControllers

import ru.avem.kspem.communication.model.devices.trm202.TRM202Model.Companion.T_1
import ru.avem.kspem.communication.model.devices.trm202.TRM202Model.Companion.T_2
import ru.avem.kspem.controllers.CustomController
import ru.avem.kspem.data.objectModel
import ru.avem.kspem.data.protocolModel
import ru.avem.kspem.utils.LogTag
import ru.avem.kspem.utils.sleep
import ru.avem.kspem.view.expViews.MGRView
import ru.avem.stand.utils.autoformat
import tornadofx.isDouble


class MGRController : CustomController() {
    override val model: MGRView by inject()
    override val name = model.name

    override fun start() {
        model.clearTables()
        super.start()


        if (isExperimentRunning) {
            pr200.km7(true)
            appendMessageToLog(LogTag.MESSAGE, "Инициализация Меггер...")
            with(cs02) {
                checkResponsibility()
                if (isExperimentRunning) {
                    initButtonPost()
                }

                if (isResponding) {
                    setVoltage(500)
                    var timer = 90.0
                    while (isExperimentRunning && timer > 0) {
                        sleep(100)
                        timer -= 0.1
                    }
                    val measuredR60 = readData()[0].toDouble()
                    val measuredUr = readData()[1].toDouble()
                    val measuredAbs = readData()[2].toDouble()
                    val measuredR15 = readData()[3].toDouble()

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
                        timer = 90.0
                        while (isExperimentRunning && timer > 0) {
                            sleep(100)
                            timer -= 0.1
                        }
                    }

                } else {
                    cause = "Меггер не отвечает"
                }
            }
        }
//        finalizeExperiment()
        pr200.km7(false)


        when (cause) {
            "" -> {
                if (model.data.K_ABS.value.isDouble()) {
                    if (model.data.K_ABS.value.toDouble() < 1.3) {
                        appendMessageToLog(LogTag.ERROR, "Измеренный kABS < 1.3")
                        model.data.result.value = "Не соответствует"
                        cause = "мегер"
                        enableButtons()
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