package ru.avem.kspem.app

import javafx.scene.input.KeyCombination
import javafx.stage.Stage
import javafx.stage.StageStyle
import ru.avem.kserialpooler.communication.PortDiscover
import ru.avem.kspem.database.validateDB
import ru.avem.kspem.view.AuthorizationView
import ru.avem.kspem.view.MainView
import ru.avem.kspem.view.Styles
import tornadofx.App

class Main : App(AuthorizationView::class, Styles::class) {

    companion object {
        var isAppRunning = true
    }

    override fun init() {
        validateDB()
    }


    override fun start(stage: Stage) {
        stage.isFullScreen = true
        stage.isResizable = false
        stage.initStyle(StageStyle.TRANSPARENT)
        stage.fullScreenExitKeyCombination = KeyCombination.NO_MATCH
        super.start(stage)
//        FX.primaryStage.icons += Image("icon.png")
    }


    override fun stop() {
        isAppRunning = false
        PortDiscover.isPortDiscover = false
        super.stop()
    }
}
