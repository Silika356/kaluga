package ru.avem.kspem.view

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.event.EventHandler
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.stage.Modality
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.kspem.controllers.MainViewController
import ru.avem.kspem.controllers.printerController.TSCPrinter
import ru.avem.kspem.data.MainViewTable
import ru.avem.kspem.data.objectModel
import ru.avem.kspem.data.protocolModel
import ru.avem.kspem.database.entities.TestObjects
import ru.avem.kspem.protocol.PrinterProtocol
import ru.avem.kspem.utils.Singleton
import ru.avem.kspem.utils.State
import ru.avem.kspem.view.Styles.Companion.mainTheme
import tornadofx.*
import tornadofx.controlsfx.errorNotification
import tornadofx.controlsfx.infoNotification
import java.nio.file.Path
import java.nio.file.Paths
import java.text.SimpleDateFormat
import kotlin.system.exitProcess
import kotlin.time.ExperimentalTime


class MainView : View("КСПЭМ") {

    private val controller: MainViewController by inject()

    private var cbObjects: ComboBox<TestObjects> by singleAssign()
    var comIndicate: Circle by singleAssign()
    private var sliderColor: Slider by singleAssign()
    private var sliderSaturation: Slider by singleAssign()
    private var sliderBrightness: Slider by singleAssign()

    var circlePR200: Circle by singleAssign()
    var tfSerial: TextField by singleAssign()
    var tfDate: TextField by singleAssign()
    var btnAuto: Button by singleAssign()
    var cbList = mutableListOf<CheckBox>()
    var labelType: Label by singleAssign()

    val tableData = MainViewTable()

    private var vBoxExps: VBox by singleAssign()

    override val configPath: Path = Paths.get("cfg/app.properties")

    @OptIn(ExperimentalTime::class)
    override fun onDock() {
        sliderColor.value = config["COLOR"].toString().toDouble()
        sliderSaturation.value = config["SATURATION"].toString().toDouble()
        sliderBrightness.value = config["BRIGHTNESS"].toString().toDouble()

        this@MainView.root.style {
            setColorAndSave()
        }

        cbList.forEach { it.isSelected = false }
        getObjectItems()
        getObjectData()
        super.onDock()
    }

    override val root = borderpane {
        style {
            baseColor = Color.hsb(Singleton.color1, Singleton.color2, Singleton.color3)
        }
        top {
            menubar {
                menu("Меню") {
                    item("Экран авторизации") {
                        graphic = MaterialDesignIconView(MaterialDesignIcon.NATURE_PEOPLE).apply {
                            glyphSize = 48
                            fill = c("#FF0000")
                        }
                        action {
                            replaceWith<AuthorizationView>()
                        }
                    }
                    item("Выход") {
                        graphic = MaterialDesignIconView(MaterialDesignIcon.EXIT_TO_APP).apply {
                            glyphSize = 48
                            fill = c("#FF0000")
                        }
                        action {
                            exitProcess(0)
                        }
                    }
                }
                menu("База данных") {
                    item("Пользователи") {
                        graphic = MaterialDesignIconView(MaterialDesignIcon.ACCOUNT).apply {
                            glyphSize = 48
                        }
                        action {
                            find<UserEditorWindow>().openModal(
                                modality = Modality.APPLICATION_MODAL,
                                escapeClosesWindow = true,
                                resizable = false,
                                owner = this@MainView.currentWindow
                            )
                        }
                    }
                    item("Объект испытания") {
                        graphic = MaterialDesignIconView(MaterialDesignIcon.ETHERNET_CABLE).apply {
                            glyphSize = 48
                        }
                        action {
                            replaceWith(find<ObjectEditorWindow>())
                        }
                    }
                    item("Протоколы") {
                        graphic = MaterialDesignIconView(MaterialDesignIcon.FORMAT_LIST_NUMBERS).apply {
                            glyphSize = 48
                        }
                        action {
                            find<ProtocolListWindow>().openModal(
                                modality = Modality.APPLICATION_MODAL,
                                escapeClosesWindow = true,
                                resizable = false,
                                owner = this@MainView.currentWindow
                            )
                        }
                    }
                }
                menu("Выбор темы") {
                    item("Цвет") {
                        sliderColor = slider(0, 360) {
                            onMouseDragged = EventHandler {
                                this@borderpane.style {
                                    setColorAndSave()
                                }
                            }
                            onMousePressed = EventHandler {
                                this@borderpane.style {
                                    setColorAndSave()
                                }
                            }
                            onMouseReleased = EventHandler {
                                this@borderpane.style {
                                    setColorAndSave()
                                }
                            }
                        }
                    }
                    item("Насыщенность") {
                        sliderSaturation = slider(0, 1) {
                            onMouseDragged = EventHandler {
                                this@borderpane.style {
                                    setColorAndSave()
                                }
                            }
                            onMousePressed = EventHandler {
                                this@borderpane.style {
                                    setColorAndSave()
                                }
                            }
                            onMouseReleased = EventHandler {
                                this@borderpane.style {
                                    setColorAndSave()
                                }
                            }
                        }
                    }
                    item("Яркость") {
                        sliderBrightness = slider(0, 1) {
                            onMouseDragged = EventHandler {
                                this@borderpane.style {
                                    setColorAndSave()
                                }
                            }
                            onMousePressed = EventHandler {
                                this@borderpane.style {
                                    setColorAndSave()
                                }
                            }
                            onMouseReleased = EventHandler {
                                this@borderpane.style {
                                    setColorAndSave()
                                }
                            }
                        }
                    }
                }
                menu("Информация") {
                    item("Версия ПО") {
                        graphic = MaterialDesignIconView(MaterialDesignIcon.INFORMATION_OUTLINE).apply {
                            glyphSize = 48
                            fill = c("#0000FF")
                        }
                        action {
                            controller.showAboutUs()
                        }
                    }
                }
            }
        }
        center = anchorpane {
            vbox(32.0, Pos.CENTER) {
                anchorpaneConstraints {
                    leftAnchor = 32.0
                    rightAnchor = 32.0
                    topAnchor = 32.0
                }
                hbox(16.0, Pos.CENTER_LEFT) {
                    label("Марка двигателя:")
                    cbObjects = combobox {
                        hboxConstraints {
                            hGrow = Priority.ALWAYS
                            useMaxWidth = true
                        }
                        onAction = EventHandler {
                            if (!items.isNullOrEmpty() && selectedItem != null) {
                                getObjectData()
                            }
                        }
                        items = arrayListOf<TestObjects>().asObservable()
                    }
                }
                hbox(16.0, Pos.CENTER) {
                    label("Тип ДПР: ")
                    labelType = label() {
                        hboxConstraints {
                            hGrow = Priority.ALWAYS
                        }
                        useMaxWidth = true
                    }
                }
                hbox(16.0, Pos.CENTER) {
                    label("Заводской номер:")
                    tfSerial = textfield {
                        hboxConstraints {
                            hGrow = Priority.ALWAYS
                        }
                    }
                }
                hbox(16.0, Pos.CENTER) {
                    label("Дата изготовления:")
                    tfDate = textfield {
                        hboxConstraints {
                            hGrow = Priority.ALWAYS
                        }
                    }
                }
                tableview(observableListOf(tableData)) {
                    hboxConstraints {
                        useMaxWidth = true
                    }
                    minHeight = 102.0
                    maxHeight = 102.0
                    isMouseTransparent = true
                    column("Мощность, кВт", MainViewTable::pNom.getter).isEditable = false
                    column("Напряжение, В", MainViewTable::uNom.getter).isEditable = false
                    column("Вращающий момент, Н*м", MainViewTable::mNom.getter).isEditable = false
                    column("Частота вращения, об/мин", MainViewTable::nNom.getter).isEditable = false
                    columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                }
                vbox(16.0, Pos.CENTER_LEFT) {
                    paddingLeft = 64.0
                    checkbox("Автоматический режим") {
                        isSelected = Singleton.isAutoMod
                        onAction = EventHandler {
                            Singleton.isAutoMod = isSelected
                            with(config) {
                                set("ISAUTOMOD" to Singleton.isAutoMod)
                                save()
                            }
                        }
                    }
                    checkbox("Печать этикетки при успешном завершении") {
                        isSelected = Singleton.isPrinter
                        onAction = EventHandler {
                            Singleton.isPrinter = isSelected
                            with(config) {
                                set("ISPRINTER" to Singleton.isPrinter)
                                save()
                            }
                        }
                    }
                    button("Проверить подключение к принтеру") {
                        action {
                            TSCPrinter.INSTANCE.openport("TSC TE200")
                            if (TSCPrinter.INSTANCE.usbportqueryprinter().toInt() == -1) {
                                errorNotification("Проверка подключения к принтеру", "Принтер не подключен")
                            } else {
                                infoNotification("Проверка подключения к принтеру", "Успешно")
                            }
                            TSCPrinter.INSTANCE.closeport()
                        }
                    }
                    button("Тестовая печать") {
                        action {
                            TSCPrinter.printDocument(
                                PrinterProtocol(
                                    label1 = "ДСМ-3,0-3000-1-Д-У3 IM2081 IC41 220В",
                                    hz = "400",
                                    volt = "220",
                                    amperage = "10",
                                    kpd = "85",
                                    power = "3,0",
                                    rpm = "3000",
                                    moment = "9,6",
                                    mass = "10,5",
                                    objectNumber = "329.16 0001",
                                    date = "25.11.2016"
                                ).document
                            )
                        }
                    }
                }
                btnAuto = button("Перейти к испытанию") {
                    action {
                        TSCPrinter.INSTANCE.openport("TSC TE200")
                        if (Singleton.isPrinter && TSCPrinter.INSTANCE.usbportqueryprinter().toInt() == -1) {
                            errorNotification("Проверка подключения к принтеру", "Принтер не подключен")
                        } else {
                            if (cbObjects.selectionModel.selectedItem != null) {
                                controller.clearProtocol()
                                objectModel = cbObjects.selectionModel.selectedItem
                                protocolModel.dateManufacture = tfDate.text
                                protocolModel.time =
                                    SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis()).toString()
                                protocolModel.operator = controller.position1
                                protocolModel.objectName = cbObjects.selectionModel.selectedItem.name
                                protocolModel.serial = if (tfSerial.text.isNullOrEmpty()) "Не задан" else tfSerial.text
                                protocolModel.dataType = cbObjects.selectionModel.selectedItem.typeDpr
                                protocolModel.dataP = cbObjects.selectionModel.selectedItem.pNom
                                protocolModel.dataF = cbObjects.selectionModel.selectedItem.hzNom
                                protocolModel.dataN = cbObjects.selectionModel.selectedItem.nNom
                                protocolModel.dataU = cbObjects.selectionModel.selectedItem.uNom
                                protocolModel.dataKPD = cbObjects.selectionModel.selectedItem.kpd
                                replaceWith(find<ExpView>())
                            } else {
                                runLater {
                                    errorNotification("Ошибка", "Не выбран ОИ")
                                }
                            }
                        }
                        TSCPrinter.INSTANCE.closeport()
                    }
                }
            }
        }

        bottom =
            hbox(spacing = 16) {
                hboxConstraints {
                    paddingBottom = 8.0
                }
                paddingLeft = 32.0
                alignment = Pos.CENTER_LEFT
                comIndicate = circle(radius = 20) {
                    fill = State.INTERMEDIATE.c
                    stroke = c("black")
                    isSmooth = true
                }
                label(" Связь БСУ") {
                    hboxConstraints {
                        hGrow = Priority.ALWAYS
                    }
                }
                separator(Orientation.VERTICAL)
                circlePR200 = circle {
                    radius = 20.0
                    fill = State.INTERMEDIATE.c
                    stroke = c("black")
                    isSmooth = true
                }
                label("ПР200") {
                    hboxConstraints {
                        hgrow = Priority.ALWAYS
                    }
                }
//                separator(Orientation.VERTICAL)
//                circlePM130 = circle {
//                    radius = 20.0
//                    fill = State.INTERMEDIATE.c
//                    stroke = c("black")
//                    isSmooth = true
//                }
//                label("PM130") {
//                    hboxConstraints {
//                        hgrow = Priority.ALWAYS
//                    }
//                }
//                separator(Orientation.VERTICAL)
//                circleAVEM = circle {
//                    radius = 20.0
//                    fill = State.INTERMEDIATE.c
//                    stroke = c("black")
//                    isSmooth = true
//                }
//                label("АВЭМ7") {
//                    hboxConstraints {
//                        hgrow = Priority.ALWAYS
//                    }
//                }
//                separator(Orientation.VERTICAL)
//                circleLATR = circle {
//                    radius = 20.0
//                    fill = State.INTERMEDIATE.c
//                    stroke = c("black")
//                    isSmooth = true
//                }
//                label("АРН") {
//                    hboxConstraints {
//                        hgrow = Priority.ALWAYS
//                    }
//                }
            }
    }.addClass(mainTheme)


    fun getObjectItems() {
        clearTable()
        cbObjects.items = arrayListOf<TestObjects>().asObservable()
        transaction {
            TestObjects.all().forEach {
                cbObjects.items.add(it)
            }
        }
        cbObjects.items = cbObjects.items.reversed().asObservable()
        cbObjects.selectionModel.selectFirst()
    }

    private fun getObjectData() {
        labelType.text = cbObjects.selectionModel.selectedItem.typeDpr
        with(cbObjects.selectionModel.selectedItem) {
            tableData.pNom.value = pNom
            tableData.uNom.value = uNom
            tableData.mNom.value = mNom
            tableData.nNom.value = nNom
        }
    }

    private fun clearTable() {
    }

    private fun InlineCss.setColorAndSave() {
        baseColor = Color.hsb(sliderColor.value, sliderSaturation.value, sliderBrightness.value)
        with(config) {
            set("COLOR" to sliderColor.value)
            set("SATURATION" to sliderSaturation.value)
            set("BRIGHTNESS" to sliderBrightness.value)
            Singleton.color1 = sliderColor.value
            Singleton.color2 = sliderSaturation.value
            Singleton.color3 = sliderBrightness.value
            save()
        }
    }

    fun setToDefault() {
        runLater {
        }
    }
}
