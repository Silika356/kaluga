package ru.avem.kspem.view

import javafx.geometry.Pos
import javafx.scene.paint.Paint
import javafx.scene.text.FontWeight
import tornadofx.*

class Styles : Stylesheet() {

    companion object {
        val lineChart by cssclass()
        val redText by cssclass()
        val greenText by cssclass()
        val mainTheme by cssclass()
        val expTheme by cssclass()
        val blackThemeOnlyColor by cssclass()
        val medium by cssclass()
        val extraHard by cssclass()
        val maxTemp by cssclass()
        val startStop by cssclass()
        val anchorPaneBorders by cssclass()
        val anchorPaneStatusColor by cssclass()
        val roundButton by cssclass()
        val powerButtons by cssclass()
        val kVArPowerButtons by cssclass()
        val tableRowCell by cssclass()
        val vboxTextArea by cssclass()
        val alert by cssclass()
        val rowSel by cssclass()
    }

    init {


        alert {
            baseColor = c("#222")
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
        }

        mainTheme {
            fontSize = 24.px
            fontWeight = FontWeight.BOLD
            checkBox {
            }
            comboBox {
                fontSize = 20.px
                fontWeight = FontWeight.BOLD
            }
            textField {
                fontSize = 20.px
                fontWeight = FontWeight.BOLD
            }
            tableColumn {
                alignment = Pos.CENTER
                fontWeight = FontWeight.BOLD
                fontSize = 25.px
            }
        }
        expTheme {
            fontSize = 30.px
            fontWeight = FontWeight.BOLD
            checkBox {
            }
            comboBox {
                fontSize = 30.px
                fontWeight = FontWeight.BOLD
            }
            textField {
                fontSize = 30.px
                fontWeight = FontWeight.BOLD
            }
            tableColumn {
                alignment = Pos.CENTER
                fontWeight = FontWeight.BOLD
                fontSize = 30.px
            }
        }
        blackThemeOnlyColor {
//            baseColor = c("#b5bec9")
//            baseColor = Color.hsb(Singleton.color1, Singleton.color2, Singleton.color3)
//            accentColor = c("#f88379")
//            focusColor = Paint.valueOf("#f88379")
        }

        tabPane {
            tab {
                focusColor = Paint.valueOf("#00000000") //transparent
            }
        }

        redText {
            backgroundColor += c("#FF0000")
            textFill = c("#FFFFFF")
        }

        greenText {
            backgroundColor += c("#00FF00")
            textFill = c("#FFFFFF")
        }

        medium {
            fontSize = 18.px
            fontWeight = FontWeight.BOLD
        }


        maxTemp {
            fontSize = 30.px
            fontWeight = FontWeight.BOLD
        }

        extraHard {
            fontSize = 24.px
            fontWeight = FontWeight.BOLD
        }

        startStop {
            fontSize = 30.px
            fontWeight = FontWeight.EXTRA_BOLD
//            baseColor = c("#333")
        }

        powerButtons {
            fontSize = 18.px
//            baseColor = c("#2178CC")
            baseColor = c("#222")
            prefWidth = 50.px
        }

        kVArPowerButtons {
            fontSize = 18.px
            baseColor = c("#60C3CC")
            prefWidth = 50.px
        }

        anchorPaneBorders {
            borderColor += CssBox(
                top = c("grey"),
                bottom = c("grey"),
                left = c("grey"),
                right = c("grey")
            )
        }

        anchorPaneStatusColor {
            backgroundColor += c("#B4AEBF")
        }

        roundButton {
            backgroundRadius += CssBox(
                top = 30.px,
                bottom = 30.px,
                left = 30.px,
                right = 30.px
            )
        }




        tableRowCell {
            cellSize = 50.px
        }

        rowSel {
            tableRowCell {
                and(selected) {
                    backgroundColor += c("green")
                }
            }
        }

        vboxTextArea {
//            backgroundColor += c("#6696bd")

            backgroundColor += c("#333")
        }

        lineChart {
            chartSeriesLine {
                backgroundColor += c("red")
                stroke = c("red")
            }
            chartLineSymbol {
                backgroundColor += c("red")
            }
        }
        checkBox {
            selected {
                mark {
                    backgroundColor += c("#FF0000")
                }
            }
        }
    }

}
