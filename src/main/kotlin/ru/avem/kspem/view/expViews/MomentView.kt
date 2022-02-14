package ru.avem.kspem.view.expViews

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.geometry.Pos
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import tornadofx.*


class RUNNINGView : View() {
    val name = "Определение момента сопротивления вращению"
    val data = MomentData()
    val series = XYChart.Series<Number, Number>()

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
                column("Момент, мН*м", MomentData::m.getter).isEditable = false
                column("n, об/мин", MomentData::n.getter).isEditable = false
                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            }
            tableview(observableListOf(data)) {
                hboxConstraints {
                    useMaxWidth = true
                }
                minHeight = 120.0
                maxHeight = 120.0
                isMouseTransparent = true
                column("Сред. момент, мН*м", MomentData::mAvg.getter).isEditable = false
                column("Макс. момент, мН*м", MomentData::mMax.getter).isEditable = false
                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            }
            tableview(observableListOf(data)) {
                hboxConstraints {
                    useMaxWidth = true
                }
                minHeight = 120.0
                maxHeight = 120.0
                isMouseTransparent = true
//                column("Отклонение, %", MomentData::mDeviation.getter)
                column("Результат", MomentData::result.getter)
                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            }
        }
        linechart("Момент сопротивления вращению", NumberAxis(), NumberAxis()) {
            hboxConstraints {
                hgrow = Priority.ALWAYS
                vgrow = Priority.ALWAYS
            }
            useMaxHeight = true
            maxWidth = 1200.0
            animated = false
            createSymbols = false
            isLegendVisible = false
            xAxis.label = ("Время,с")
            yAxis.label = ("Момент, мН*м")
            data.add(series)
        }
    }

    fun clearTables() {
        data.n.value = ""
        data.m.value = ""
        data.mAvg.value = ""
        data.mMax.value = ""
        data.result.value = ""
        runLater {
            series.data.clear()
        }
    }
}
data class MomentData(
    val n: StringProperty = SimpleStringProperty(""),
    val m: StringProperty = SimpleStringProperty(""),
    val mAvg: StringProperty = SimpleStringProperty(""),
    val mMax: StringProperty = SimpleStringProperty(""),
    val result: StringProperty = SimpleStringProperty("")
)