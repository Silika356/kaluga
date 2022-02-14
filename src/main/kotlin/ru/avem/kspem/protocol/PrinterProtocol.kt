package ru.avem.kspem.protocol

import ru.avem.kstpl.dsl.*

class PrinterProtocol(
    private val label1: String,
    private val hz: String,
    private val volt: String,
    private val amperage: String,
    private val kpd: String,
    private val power: String,
    private val rpm: String,
    private val moment: String,
    private val mass: String,
    private val objectNumber: String,
    private val date: String
) {
    val document = document {
        size {
            width = 45
            height = 20
        }
        direction {
            feedDirection = Directions.Rotation.Rotated
        }
        codepage(Codepages.UTF8)
        cls()
        text {
            x = 90
            y = 0
            xMultiplication = 8.0
            yMultiplication = 8.0
            text = "АО КЭМЗ Сделано в России"
        }
        text {
            x = 50
            y = 20
            xMultiplication = 7.0
            yMultiplication = 7.0
            text = "Двигатель синхронный многополюсный"
        }
        text {
            x = 35
            y = 40
            xMultiplication = 7.0
            yMultiplication = 7.0
            text = label1
        }
        text {
            x = 65
            y = 60
            xMultiplication = 7.0
            yMultiplication = 7.0
            text = "ТУ 27.11-002-10839399-2017-ЛУ"
        }
        text {
            x = 15
            y = 80
            xMultiplication = 6.7
            yMultiplication = 6.7
            text = "${hz}Гц;${volt}В;${amperage}А;КПД=${kpd}%;Кл.из.F;Сх.соед.'Y'"
        }
        text {
            x = 15
            y = 100
            xMultiplication = 7.0
            yMultiplication = 7.0
            text = "${power} кВт; ${rpm} об/мин; ${moment} Нм; IP54; ${mass.replace(".",",")} кг"
        }
        text {
            x = 10
            y = 120
            xMultiplication = 7.0
            yMultiplication = 7.0
            text = "№${objectNumber} Дата изготовления: ${date}"
        }
        print {
            labelCount = 1
            copies = 1
        }
    }
}
