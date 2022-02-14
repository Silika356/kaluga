package ru.avem.kspem.view.expViews

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.geometry.Pos
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import ru.avem.kspem.data.dpr
import ru.avem.kspem.data.protocolModel
import tornadofx.*


class DprView : View() {
    val name = "Определение параметров ДПР"
    val data = DprData()

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
            tableview(observableListOf(data)) {
                hboxConstraints {
                    useMaxWidth = true
                }
                minHeight = 120.0
                maxHeight = 120.0
                isMouseTransparent = true
                column("Uamp SinP, В", DprData::ampSinP.getter).isEditable = false
                column("Uamp CosN, В", DprData::ampCosN.getter).isEditable = false
                column("Uamp SinN, В", DprData::ampSinN.getter).isEditable = false
                column("Uamp CosP, В", DprData::ampCosP.getter).isEditable = false
                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            }
            tableview(observableListOf(data)) {
                hboxConstraints {
                    useMaxWidth = true
                }
                minHeight = 120.0
                maxHeight = 120.0
                isMouseTransparent = true
                column("Umax SinP, В", DprData::maxSinP.getter).isEditable = false
                column("Umax CosN, В", DprData::maxCosN.getter).isEditable = false
                column("Umax SinN, В", DprData::maxSinN.getter).isEditable = false
                column("Umax CosP, В", DprData::maxCosP.getter).isEditable = false
                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            }
            tableview(observableListOf(data)) {
                hboxConstraints {
                    useMaxWidth = true
                }
                minHeight = 120.0
                maxHeight = 120.0
                isMouseTransparent = true
                column("Umin SinP, В", DprData::minSinP.getter).isEditable = false
                column("Umin CosN, В", DprData::minCosN.getter).isEditable = false
                column("Umin SinN, В", DprData::minSinN.getter).isEditable = false
                column("Umin CosP, В", DprData::minCosP.getter).isEditable = false
                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            }
            tableview(observableListOf(data)) {
                hboxConstraints {
                    useMaxWidth = true
                }
                minHeight = 120.0
                maxHeight = 120.0
                isMouseTransparent = true
                column("∠AB, SinP, эл.гр.", DprData::dprSinP.getter).isEditable = false
                column("∠AB, CosN, эл.гр.", DprData::dprCosN.getter).isEditable = false
                column("∠AB, SinN, эл.гр.", DprData::dprSinN.getter).isEditable = false
                column("∠AB, CosP, эл.гр.", DprData::dprCosP.getter).isEditable = false
                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            }
            tableview(observableListOf(data)) {
                hboxConstraints {
                    useMaxWidth = true
                }
                minHeight = 120.0
                maxHeight = 120.0
                isMouseTransparent = true
                column("Разброс Umax, %", DprData::deviationMax.getter).isEditable = false
                column("Разброс Umin, %", DprData::deviationMin.getter).isEditable = false
                column("Отклонение, мс?", DprData::deviationDpr.getter).isEditable = false
                column("Частота ДПР", DprData::hz.getter).isEditable = false
                column("Результат", DprData::result.getter).isEditable = false
                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            }
        }
    }

    fun clearTables() {
        data.dprCosN.value = ""
        data.dprSinN.value = ""
        data.dprCosP.value = ""
        data.dprSinP.value = ""
        data.ampSinP.value = ""
        data.ampSinN.value = ""
        data.ampCosP.value = ""
        data.ampCosN.value = ""
        data.maxSinP.value = ""
        data.maxSinN.value = ""
        data.maxCosP.value = ""
        data.maxCosN.value = ""
        data.minSinP.value = ""
        data.minSinN.value = ""
        data.minCosP.value = ""
        data.minCosN.value = ""
        data.deviationMin.value = ""
        data.deviationMax.value = ""
        data.deviationDpr.value = ""
        data.hz.value = ""
        data.result.value = ""
    }
}
data class DprData(
    val ampSinP: StringProperty = SimpleStringProperty(""),
    val ampSinN: StringProperty = SimpleStringProperty(""),
    val ampCosP: StringProperty = SimpleStringProperty(""),
    val ampCosN: StringProperty = SimpleStringProperty(""),

    val maxSinP: StringProperty = SimpleStringProperty(""),
    val maxSinN: StringProperty = SimpleStringProperty(""),
    val maxCosP: StringProperty = SimpleStringProperty(""),
    val maxCosN: StringProperty = SimpleStringProperty(""),

    val minSinP: StringProperty = SimpleStringProperty(""),
    val minSinN: StringProperty = SimpleStringProperty(""),
    val minCosP: StringProperty = SimpleStringProperty(""),
    val minCosN: StringProperty = SimpleStringProperty(""),

    val dprSinP: StringProperty = SimpleStringProperty(""),
    val dprSinN: StringProperty = SimpleStringProperty(""),
    val dprCosP: StringProperty = SimpleStringProperty(""),
    val dprCosN: StringProperty = SimpleStringProperty(""),

    val deviationMin: StringProperty = SimpleStringProperty(""),
    val deviationMax: StringProperty = SimpleStringProperty(""),
    val deviationDpr: StringProperty = SimpleStringProperty(""),
    val hz: StringProperty = SimpleStringProperty(""),
    val result: StringProperty = SimpleStringProperty("")
)