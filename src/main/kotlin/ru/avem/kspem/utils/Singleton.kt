package ru.avem.kspem.utils

import ru.avem.kspem.database.entities.Protocol
import ru.avem.kspem.database.entities.TestObjects


object Singleton {
    lateinit var currentProtocol: Protocol
    lateinit var currentTestItem: TestObjects
    var color1: Double = 20.0
    var color2: Double = 0.5
    var color3: Double = 1.0
}
