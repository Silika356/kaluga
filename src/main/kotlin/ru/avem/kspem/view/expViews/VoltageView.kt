package ru.avem.kspem.view.expViews

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.geometry.Pos
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import tornadofx.*


class VoltageView : View() {
    val name = "Определение линейных напряжений двигателя"
    val data = VoltageData()
//    val seriesAB = XYChart.Series<Number, Number>()

    override fun onDock() {
        super.onDock()
        runLater {
            clearTables()
        }
    }

    override val root = hbox(16.0, Pos.CENTER) {
        padding = insets(8)

        hboxConstraints {
            hGrow = Priority.ALWAYS
        }
        vbox(16.0, Pos.CENTER) {
            hboxConstraints {
                hgrow = Priority.ALWAYS
                vgrow = Priority.ALWAYS
                useMaxWidth = true
            }
            label("Измеренные значения")
            tableview(observableListOf(data)) {
                hboxConstraints {
                    useMaxWidth = true
                }
                minHeight = 120.0
                maxHeight = 120.0
                isMouseTransparent = true
                column("Uab, В", VoltageData::uAB.getter).isEditable = false
                column("Ubc, В", VoltageData::uBC.getter).isEditable = false
                column("Uca, В", VoltageData::uCA.getter).isEditable = false
                column("Частота, Гц", VoltageData::freq.getter).isEditable = false
                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            }
            label("Приведенные к 1000 об/мин")
            tableview(observableListOf(data)) {
                hboxConstraints {
                    useMaxWidth = true
                }
                minHeight = 120.0
                maxHeight = 120.0
                isMouseTransparent = true
                column("Uab, В", VoltageData::uABcalc.getter).isEditable = false
                column("Ubc, В", VoltageData::uBCcalc.getter).isEditable = false
                column("Uca, В", VoltageData::uCAcalc.getter).isEditable = false
                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            }
            tableview(observableListOf(data)) {
                hboxConstraints {
                    useMaxWidth = true
                }
                minHeight = 120.0
                maxHeight = 120.0
                isMouseTransparent = true
                column("Частота, об/мин", VoltageData::rpm.getter)
                column("Разброс, %", VoltageData::deviation.getter)
                column("Результат", VoltageData::result.getter)
                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            }
        }
//        linechart("Характеристика разгона", NumberAxis(), NumberAxis()) {
//            hboxConstraints {
//                hgrow = Priority.ALWAYS
//                vgrow = Priority.ALWAYS
//            }
//            useMaxHeight = true
//            maxWidth = 1200.0
//            animated = true
//            createSymbols = false
//            isLegendVisible = false
//            yAxis.label = ("Напряжение, В")
//            xAxis.label = ("Частота , об/мин")
//            data.add(seriesAB)
//        }
    }

    fun clearTables() {
        data.uAB.value = ""
        data.uBC.value = ""
        data.uCA.value = ""
        data.uABcalc.value = ""
        data.uBCcalc.value = ""
        data.uCAcalc.value = ""
        data.deviation.value = ""
        data.result.value = ""
    }
}
data class VoltageData(
    val uAB: StringProperty = SimpleStringProperty(""),
    val uBC: StringProperty = SimpleStringProperty(""),
    val uCA: StringProperty = SimpleStringProperty(""),
    val rpm: StringProperty = SimpleStringProperty(""),
    val freq: StringProperty = SimpleStringProperty(""),
    val uABcalc: StringProperty = SimpleStringProperty(""),
    val uBCcalc: StringProperty = SimpleStringProperty(""),
    val uCAcalc: StringProperty = SimpleStringProperty(""),
    val deviation: StringProperty = SimpleStringProperty(""),
    val result: StringProperty = SimpleStringProperty("")
)