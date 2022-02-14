package ru.avem.kspem.view

import javafx.event.EventHandler
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.kspem.data.dprType
import ru.avem.kspem.database.entities.Objects
import ru.avem.kspem.database.entities.TestObjects
import ru.avem.kspem.utils.Singleton
import tornadofx.*
import tornadofx.controlsfx.errorNotification
import tornadofx.controlsfx.infoNotification

class ObjectEditorWindow : View("Редактор объектов испытания") {

    val view: MainView by inject()

    var tfpNom: TextField by singleAssign()
    var tfmNom: TextField by singleAssign()
    var tfuNom: TextField by singleAssign()
    var tfnNom: TextField by singleAssign()
    var tfHzNom: TextField by singleAssign()
    var tfINom: TextField by singleAssign()
    var tfWeight: TextField by singleAssign()
    var tfkpd: TextField by singleAssign()
    var tfuMGR: TextField by singleAssign()
    var tfkABS: TextField by singleAssign()
    var tfvoltageDev: TextField by singleAssign()
    var tfikasDev: TextField by singleAssign()
    var tfMomentMax: TextField by singleAssign()
    var tfdprDev: TextField by singleAssign()
    var tfdprDevDegree: TextField by singleAssign()
    var tfdprPairs: TextField by singleAssign()

    var obj: VBox by singleAssign()
    var cbObjects: ComboBox<TestObjects> by singleAssign()
    var cbObjectType: ComboBox<String> by singleAssign()
    var tfObjectName: TextField by singleAssign()
    val validator = ValidationContext()
    var newCheck = false


    override fun onDock() {
        root.style {
            baseColor = Color.hsb(Singleton.color1, Singleton.color2, Singleton.color3)
        }
        getObjects()
        super.onDock()
    }

    override fun onUndock() {
        view.getObjectItems()
        super.onUndock()
    }


    override val root = anchorpane {
        hbox(16.0, Pos.CENTER_LEFT) {
            anchorpaneConstraints {
                leftAnchor = 16.0
                rightAnchor = 16.0
                topAnchor = 16.0
            }
            vbox(16.0, Pos.BASELINE_LEFT) {
                minWidth = 700.0
                maxWidth = 700.0
                paddingTop = 300.0
                paddingLeft = 300.0
                checkbox("Создать новый") {
                    onAction = EventHandler {
                        if (isSelected) {
                            newCheck = true
                            cbObjects.isVisible = false
                            cbObjectType.isDisable = false
                            tfObjectName.show()
                        } else {
                            cbObjects.isVisible = true
                            newCheck = false
                            tfObjectName.text = ""
                            cbObjectType.isDisable = true
                            tfObjectName.hide()
                        }
                    }
                }
                label("Марка двигателя")
                tfObjectName = textfield {
                    validator.addValidator(this) {
                        if (it == null && newCheck) {
                            error("Обязательное поле")
                        } else null
                    }
                    hide()
                }
                cbObjects = combobox() {
                    useMaxWidth = true
                    onAction = EventHandler {
                        if (cbObjects.selectionModel.selectedItem != null) {
                            cbObjectType.selectionModel.select(this.selectionModel.selectedItem.typeDpr)
                            getObjectData()
                        }
                    }
                }
                label()
                separator()
                label("Тип ДПР")
                cbObjectType = combobox() {
                    items = observableListOf(dprType.digital, dprType.dif, dprType.sin)
                    onAction = EventHandler {
                        if (!this.selectedItem.isNullOrEmpty()) {
                            obj.show()
                        } else {
                            obj.hide()
                        }
                    }
                    useMaxWidth = true
                    isDisable = true
                }
                label()
                separator()
                hbox(16.0, Pos.CENTER) {
                    useMaxWidth = true
                    button("Сохранить") {
                        useMaxWidth = true
                        action {
                            saveItem()
                        }
                    }
                    button("Удалить") {
                        useMaxWidth = true
                        action {
                            removeItem()
                        }
                    }
                }
                separator()
                label()
                hbox(16.0, Pos.CENTER) {
                    button("Выход") {
                        action {
                            replaceWith(find<MainView>())
                        }
                    }
                }
            }
            label()
            separator(Orientation.VERTICAL)
            label()
            obj = vbox(16.0, Pos.CENTER) {
                paddingTop = 50.0
                hbox(16.0, Pos.CENTER) {
                    label("Мощность, кВт") {
                        hboxConstraints {
                            hGrow = Priority.ALWAYS
                        }
                        useMaxWidth = true
                    }
                    tfpNom = textfield {
                        validator.addValidator(this) {
                            if (it?.toDoubleOrNull() == null) {
                                error("Обязательное поле")
                            } else if ((it.toDouble()) < 0 || (it.toDouble()) > 18) {
                                error("Значение не в диапазоне 0 — 18")
                            } else null
                        }
                    }
                }
                hbox(16.0, Pos.CENTER) {
                    label("Напряжение, В") {
                        hboxConstraints {
                            hGrow = Priority.ALWAYS
                        }
                        useMaxWidth = true
                    }
                    tfuNom = textfield {
                        validator.addValidator(this) {
                            if (it?.toDoubleOrNull() == null) {
                                error("Обязательное поле")
                            } else if ((it.toDouble()) < 0 || (it.toDouble()) > 500) {
                                error("Значение не в диапазоне 0 — 500")
                            } else null
                        }
                    }
                }
                hbox(16.0, Pos.CENTER) {
                    label("Частота напряжения, Гц") {
                        hboxConstraints {
                            hGrow = Priority.ALWAYS
                        }
                        useMaxWidth = true
                    }
                    tfHzNom = textfield {
                        validator.addValidator(this) {
                            if (it?.toDoubleOrNull() == null) {
                                error("Обязательное поле")
                            } else if ((it.toDouble()) < 0 || (it.toDouble()) > 600) {
                                error("Значение не в диапазоне 0 — 600")
                            } else null
                        }
                    }
                }
                hbox(16.0, Pos.CENTER) {
                    label("Ток номинальный, А") {
                        hboxConstraints {
                            hGrow = Priority.ALWAYS
                        }
                        useMaxWidth = true
                    }
                    tfINom = textfield {
                        validator.addValidator(this) {
                            if (it?.toDoubleOrNull() == null) {
                                error("Обязательное поле")
                            } else if ((it.toDouble()) < 0 || (it.toDouble()) > 50) {
                                error("Значение не в диапазоне 0 — 50")
                            } else null
                        }
                    }
                }
                hbox(16.0, Pos.CENTER) {
                    label("Вращающий момент, Н*м") {
                        hboxConstraints {
                            hGrow = Priority.ALWAYS
                        }
                        useMaxWidth = true
                    }
                    tfmNom = textfield {
                        validator.addValidator(this) {
                            if (it?.toDoubleOrNull() == null) {
                                error("Обязательное поле")
                            } else if ((it.toDouble()) < 0 || (it.toDouble()) > 100) {
                                error("Значение не в диапазоне 0 — 100")
                            } else null
                        }
                    }
                }
                hbox(16.0, Pos.CENTER) {
                    label("Частота вращения, об/мин") {
                        hboxConstraints {
                            hGrow = Priority.ALWAYS
                        }
                        useMaxWidth = true
                    }
                    tfnNom = textfield {
                        validator.addValidator(this) {
                            if (it?.toDoubleOrNull() == null) {
                                error("Обязательное поле")
                            } else if ((it.toDouble()) < 0 || (it.toDouble()) > 3000) {
                                error("Значение не в диапазоне 0 — 3000")
                            } else null
                        }
                    }
                }
                hbox(16.0, Pos.CENTER) {
                    label("КПД, %") {
                        hboxConstraints {
                            hGrow = Priority.ALWAYS
                        }
                        useMaxWidth = true
                    }
                    tfkpd = textfield {
                        validator.addValidator(this) {
                            if (it?.toDoubleOrNull() == null) {
                                error("Обязательное поле")
                            } else if ((it.toDouble()) < 0 || (it.toDouble()) > 100) {
                                error("Значение не в диапазоне 0 — 100")
                            } else null
                        }
                    }
                }
                hbox(16.0, Pos.CENTER) {
                    label("Масса, кг") {
                        hboxConstraints {
                            hGrow = Priority.ALWAYS
                        }
                        useMaxWidth = true
                    }
                    tfWeight = textfield {
                        validator.addValidator(this) {
                            if (it?.toDoubleOrNull() == null) {
                                error("Обязательное поле")
                            } else if ((it.toDouble()) < 0 || (it.toDouble()) > 100) {
                                error("Значение не в диапазоне 0 — 100")
                            } else null
                        }
                    }
                }
                separator()
                hbox(16.0, Pos.CENTER) {
                    label("Напряжение мегаомметра") {
                        hboxConstraints {
                            hGrow = Priority.ALWAYS
                        }
                        useMaxWidth = true
                    }
                    tfuMGR = textfield {
                        validator.addValidator(this) {
                            if (it?.toDoubleOrNull() == null) {
                                error("Обязательное поле")
                            } else if ((it.toDouble()) < 100 || (it.toDouble()) > 1000) {
                                error("Значение не в диапазоне 100 — 1000")
                            } else null
                        }
                    }
                }
                hbox(16.0, Pos.CENTER) {
                    label("Допустимый kABS мегаомметра") {
                        hboxConstraints {
                            hGrow = Priority.ALWAYS
                        }
                        useMaxWidth = true
                    }
                    tfkABS = textfield {
                        validator.addValidator(this) {
                            if (it?.toDoubleOrNull() == null) {
                                error("Обязательное поле")
                            } else if ((it.toDouble()) < 1 || (it.toDouble()) > 4) {
                                error("Значение не в диапазоне 1 — 4")
                            } else null
                        }
                    }
                }
                hbox(16.0, Pos.CENTER) {
                    label("Допустимый разброс сопротивлений") {
                        hboxConstraints {
                            hGrow = Priority.ALWAYS
                        }
                        useMaxWidth = true
                    }
                    tfikasDev = textfield {
                        validator.addValidator(this) {
                            if (it?.toDoubleOrNull() == null) {
                                error("Обязательное поле")
                            } else if ((it.toDouble()) < 1 || (it.toDouble()) > 100) {
                                error("Значение не в диапазоне 1 — 100")
                            } else null
                        }
                    }
                }
                hbox(16.0, Pos.CENTER) {
                    label("Макс. момент сопротивления вращению, мН*м") {
                        hboxConstraints {
                            hGrow = Priority.ALWAYS
                        }
                        useMaxWidth = true
                    }
                    tfMomentMax = textfield {
                        validator.addValidator(this) {
                            if (it?.toDoubleOrNull() == null) {
                                error("Обязательное поле")
                            } else if ((it.toDouble()) < 1 || (it.toDouble()) > 10000) {
                                error("Значение не в диапазоне 1 — 10000")
                            } else null
                        }
                    }
                }
                hbox(16.0, Pos.CENTER) {
                    label("Допустимый разброс напряжений ЭДС") {
                        hboxConstraints {
                            hGrow = Priority.ALWAYS
                        }
                        useMaxWidth = true
                    }
                    tfvoltageDev = textfield {
                        validator.addValidator(this) {
                            if (it?.toDoubleOrNull() == null) {
                                error("Обязательное поле")
                            } else if ((it.toDouble()) < 1 || (it.toDouble()) > 100) {
                                error("Значение не в диапазоне 1 — 100")
                            } else null
                        }
                    }
                }
                hbox(16.0, Pos.CENTER) {
                    label("Допустимый разброс напряжений ДПР") {
                        hboxConstraints {
                            hGrow = Priority.ALWAYS
                        }
                        useMaxWidth = true
                    }
                    tfdprDev = textfield {
                        validator.addValidator(this) {
                            if (it?.toDoubleOrNull() == null) {
                                error("Обязательное поле")
                            } else if ((it.toDouble()) < 1 || (it.toDouble()) > 100) {
                                error("Значение не в диапазоне 1 — 100")
                            } else null
                        }
                    }
                }
                hbox(16.0, Pos.CENTER) {
                    label("Максимальное отклонение ДПР, эл.гр.") {
                        hboxConstraints {
                            hGrow = Priority.ALWAYS
                        }
                        useMaxWidth = true
                    }
                    tfdprDevDegree = textfield {
                        validator.addValidator(this) {
                            if (it?.toDoubleOrNull() == null) {
                                error("Обязательное поле")
                            } else if ((it.toDouble()) < 1 || (it.toDouble()) > 100) {
                                error("Значение не в диапазоне 1 — 100")
                            } else null
                        }
                    }
                }
                hbox(16.0, Pos.CENTER) {
                    label("Число пар магнитных полюсов двигателя") {
                        hboxConstraints {
                            hGrow = Priority.ALWAYS
                        }
                        useMaxWidth = true
                    }
                    tfdprPairs = textfield {
                        validator.addValidator(this) {
                            if (it?.toDoubleOrNull() == null) {
                                error("Обязательное поле")
                            } else if ((it.toDouble()) < 1 || (it.toDouble()) > 32) {
                                error("Значение не в диапазоне 1 — 32")
                            } else null
                        }
                    }
                }
            }
        }
    }.addClass(Styles.mainTheme)

    fun getObjects() {
        cbObjects.items = arrayListOf<TestObjects>().asObservable()
        transaction {
            TestObjects.all().forEach {
                cbObjects.items.add(it)
            }
        }
        cbObjects.items = cbObjects.items.reversed().asObservable()
        cbObjects.selectionModel.selectFirst()
        cbObjectType.selectionModel.select(cbObjects.selectionModel.selectedItem.typeDpr)
        getObjectData()
    }

    fun getObjectData() {
        with(cbObjects.selectionModel.selectedItem) {
            runLater {
                tfpNom.text = pNom
                tfuNom.text = uNom
                tfnNom.text = nNom
                tfkpd.text = kpd
                tfHzNom.text = hzNom
                tfmNom.text = mNom
                tfINom.text = iNom
                tfWeight.text = weightNom

                tfuMGR.text = uMGR
                tfkABS.text = kABS
                tfikasDev.text = ikasDev
                tfvoltageDev.text = voltageDev
                tfdprDev.text = dprDev
                tfdprDevDegree.text = dprDevDegree
                tfMomentMax.text = momentMax
                tfdprPairs.text = dprPairs
            }
        }
    }

    fun saveItem() {
        if (validator.isValid) {
            var tempName = ""
            if (tfObjectName.text.isNullOrEmpty()) {
                tempName = cbObjects.selectionModel.selectedItem.name
            } else {
                tempName = tfObjectName.text
            }
            transaction {
                if (!TestObjects.find { Objects.name eq tempName }.empty())
                    Objects.deleteWhere {
                        Objects.name eq tempName
                    }
                TestObjects.new {
                    name = tempName
                    typeDpr = cbObjectType.selectionModel.selectedItem
                    pNom = tfpNom.text
                    uNom = tfuNom.text
                    mNom = tfmNom.text
                    nNom = tfnNom.text
                    hzNom = tfHzNom.text
                    kpd = tfkpd.text
                    weightNom = tfWeight.text
                    iNom = tfINom.text

                    uMGR = tfuMGR.text
                    kABS = tfkABS.text
                    ikasDev = tfikasDev.text
                    voltageDev = tfvoltageDev.text
                    dprDev = tfdprDev.text
                    dprDevDegree = tfdprDevDegree.text
                    momentMax = tfMomentMax.text
                    dprPairs = tfdprPairs.text
                }
            }
            getObjects()
            runLater {
                infoNotification("Сохранение", "Успешно сохранено")
            }
        } else {
            errorNotification("Ошибка", "Проверьте введенные данные")
        }
    }

    fun removeItem() {
        val tempName = cbObjects.selectionModel.selectedItem.name
        if (cbObjects.items.size > 1) {
            transaction {
                if (!TestObjects.find { Objects.name eq tempName }.empty())
                    Objects.deleteWhere {
                        Objects.name eq tempName
                    }
            }
            getObjects()
        } else {
            errorNotification("Ошибка", "Нельзя удалить последний ОИ")
        }
    }
}
