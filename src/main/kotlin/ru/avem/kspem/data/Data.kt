package ru.avem.kspem.data

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import ru.avem.kspem.app.Main
import ru.avem.kspem.controllers.expControllers.*
import ru.avem.kspem.database.entities.TestObjects

data class MainViewTable(
    val pNom: StringProperty = SimpleStringProperty(""),
    val mNom: StringProperty = SimpleStringProperty(""),
    val uNom: StringProperty = SimpleStringProperty(""),
    val nNom: StringProperty = SimpleStringProperty("")
)

var objectModel = listOf<TestObjects>().firstOrNull()

val dprType = MotorType()
data class MotorType(
    val dif: String = "Синусно-косинусный с дифференциальным выходом",
    val sin: String = "Синусно-косинусный ",
    val digital: String = "Цифровой"
)

@Volatile
var isMGR = false

val mgr = MGRController()
val ikas = IKASController()
val moment = MomentController()
val voltage = VoltageController()

val protocolModel = ProtocolModel()
data class ProtocolModel(
    //DATA//
    var objectName: String = "",
    var date: String = "",
    var time: String = "",
    var operator: String = "",
    var serial: String = "",
    var dataType: String = "",
    var dataP: String = "",
    var dataU: String = "",
    var dataI: String = "",
    var dataN: String = "",
    var dataF: String = "",
    var dataKPD: String = "",
    var dataCOS: String = "",
    //MGR//
    var mgrU: String = "",
    var mgrR15: String = "",
    var mgrR60: String = "",
    var mgrkABS: String = "",
    var mgrTemp: String = "",
    var mgrResult: String = "",
    //IKAS//
    var ikasRA: String = "",
    var ikasRB: String = "",
    var ikasRC: String = "",
    var ikasDeviation: String = "",
    var ikasResult: String = "",
    //MOMENT//
    var momentN: String = "",
    var momentAVG: String = "",
    var momentMAX: String = "",
    var momentDeviation: String = "",
    var momentResult: String = "",
    //VOLTAGE//
    var voltageN: String = "",
    var voltageF: String = "",
    var voltageUAB: String = "",
    var voltageUBC: String = "",
    var voltageUCA: String = "",
    var voltageUAB1000: String = "",
    var voltageUBC1000: String = "",
    var voltageUCA1000: String = "",
    var voltageDeviation: String = "",
    var voltageResult: String = ""
    //DPR//

)