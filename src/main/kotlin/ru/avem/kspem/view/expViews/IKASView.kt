package ru.avem.kspem.view.expViews

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.geometry.Pos
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import tornadofx.*


class IKASView : View() {
    val name = "Измерение сопротивления обмотки  постоянному току в практически холодном состоянии"
    val data = IKASData()

    override fun onDock() {
        super.onDock()
        runLater {
            clearTables()
        }
    }

    override val root = vbox(16.0, Pos.CENTER) {
        label(name)
        separator()
        padding = insets(8)
        hboxConstraints {
            hGrow = Priority.ALWAYS
        }
        label("Измеренные значения")
            tableview(observableListOf(data)) {
                hboxConstraints {
                    useMaxWidth = true
                }
                minHeight = 120.0
                maxHeight = 120.0
                isMouseTransparent = true
                column("R AB, Ом", IKASData::R1.getter).isEditable = false
                column("R BC, Ом", IKASData::R2.getter).isEditable = false
                column("R CA, Ом", IKASData::R3.getter).isEditable = false
                column("t Возд, °C", IKASData::tempAmb.getter).isEditable = false
                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            }
        label("Приведенные к 20°C фазные сопротивления")
            tableview(observableListOf(data)) {
                hboxConstraints {
                    useMaxWidth = true
                }
                minHeight = 120.0
                maxHeight = 120.0
                isMouseTransparent = true
                column("R A, Ом", IKASData::calcR1.getter).isEditable = false
                column("R B, Ом", IKASData::calcR2.getter).isEditable = false
                column("R C, Ом", IKASData::calcR3.getter).isEditable = false
                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            }
            tableview(observableListOf(data)) {
                hboxConstraints {
                    useMaxWidth = true
                }
                minHeight = 120.0
                maxHeight = 120.0
                isMouseTransparent = true
                column("Отклонение", IKASData::deviation.getter).isEditable = false
                column("Результат", IKASData::result.getter).isEditable = false
                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            }
    }
    fun clearTables() {
        data.R1.value = ""
        data.R2.value = ""
        data.R3.value = ""
        data.result.value = ""
        data.tempAmb.value = ""
        data.calcR1.value = ""
        data.calcR2.value = ""
        data.calcR3.value = ""
    }
}

data class IKASData(
    val tempAmb: StringProperty = SimpleStringProperty(""),
    val R1: StringProperty = SimpleStringProperty(""),
    val R2: StringProperty = SimpleStringProperty(""),
    val R3: StringProperty = SimpleStringProperty(""),
    val calcR1: StringProperty = SimpleStringProperty(""),
    val calcR2: StringProperty = SimpleStringProperty(""),
    val calcR3: StringProperty = SimpleStringProperty(""),
    val deviation: StringProperty = SimpleStringProperty(""),
    val result: StringProperty = SimpleStringProperty("")
)