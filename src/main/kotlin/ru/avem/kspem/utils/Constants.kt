package ru.avem.kspem.utils

import javafx.scene.paint.Color
import tornadofx.c

//enum class ExperimentType(val type: String) {
//    AC("Переменный ток") {
//        override fun toString() = type
//    },
//    DC("Постоянный ток") {
//        override fun toString() = type
//    }
//}

enum class LogTag(val c: Color) {
    MESSAGE(c("#03ba00")),
    ERROR(c("#ff0000")),
    DEBUG(c("#0087db"))
}

enum class State(val c: Color) {
    OK(c("#00dd00")),
    INTERMEDIATE(c("#6f6fff")),
    BAD(c("#fa1414")),
}

//object Measuring {
//    const val VOLT = 10
//    const val HZ = 100
//}

