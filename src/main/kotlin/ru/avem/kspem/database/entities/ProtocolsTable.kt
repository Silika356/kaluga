package ru.avem.kspem.database.entities

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import ru.avem.kspem.data.ProtocolModel
import ru.avem.kspem.database.entities.ProtocolsTable.dataP

object ProtocolsTable : IntIdTable() {
    //DATA
    var objectName = varchar("objectName", 64)
    var type = varchar("type", 64)
    var date = varchar("date", 64)
    var dateManufacture = varchar("dateManufacture", 64)
    var operator = varchar("operator", 64)
    var time = varchar("time", 64)
    var serial = varchar("serial", 64)

    var dataP = varchar("dataP", 64)
    var dataU = varchar("dataU", 64)
    var dataN = varchar("dataN", 64)
    var dataF = varchar("dataF", 64)
    var dataKPD = varchar("dataKPD", 64)
    var dataCOS = varchar("dataCOS", 64)
    //MGR//
    var mgrU = varchar("mrgU", 64)
    var mgrR15 = varchar("mgrR15", 64)
    var mgrR60 = varchar("mgrR60", 64)
    var mgrkABS = varchar("mgrkABS", 64)
    var mgrResult = varchar("mgrResult", 64)

    //IKAS//
    var ikasRA = varchar("ikasRA", 64)
    var ikasRB = varchar("ikasRB", 64)
    var ikasRC = varchar("ikasRC", 64)
    var ikasDeviation = varchar("ikasDeviation", 64)
    var ikasResult = varchar("ikasResult", 64)

    //MOMENT//
    var momentN = varchar("momentN", 64)
    var momentAVG = varchar("momentAVG", 64)
    var momentMAX = varchar("momentMAX", 64)
    var momentDeviation = varchar("momentDeviation", 64)
    var momentResult = varchar("momentResult", 64)

    //VOLTAGE//
    var voltageF = varchar("voltageF", 64)
    var voltageN = varchar("voltageN", 64)
    var voltageUAB = varchar("voltageUAB", 64)
    var voltageUBC = varchar("voltageUBC", 64)
    var voltageUCA = varchar("voltageUCA", 64)
    var voltageUAB1000 = varchar("voltageUAB1000", 64)
    var voltageUBC1000 = varchar("voltageUBC1000", 64)
    var voltageUCA1000 = varchar("voltageUCA1000", 64)
    var voltageDeviation = varchar("voltageDeviation", 64)
    var voltageResult = varchar("kzResult", 64)

    //DPR
    var dprampSinP = varchar("dprampSinP", 64)
    var dprampSinN = varchar("dprampSinN", 64)
    var dprampCosP = varchar("dprampCosP", 64)
    var dprampCosN = varchar("dprampCosN", 64)
    var dprmaxSinP = varchar("dprmaxSinP", 64)
    var dprmaxSinN = varchar("dprmaxSinN", 64)
    var dprmaxCosP = varchar("dprmaxCosP", 64)
    var dprmaxCosN = varchar("dprmaxCosN", 64)
    var dprminSinP = varchar("dprminSinP", 64)
    var dprminSinN = varchar("dprminSinN", 64)
    var dprminCosP = varchar("dprminCosP", 64)
    var dprminCosN = varchar("dprminCosN", 64)
    var dprdprSinP = varchar("dprdprSinP", 64)
    var dprdprSinN = varchar("dprdprSinN", 64)
    var dprdprCosP = varchar("dprdprCosP", 64)
    var dprdprCosN = varchar("dprdprCosN", 64)
    var dprdeviationMin = varchar("dprdeviationMin", 64)
    var dprdeviationMax = varchar("dprdeviationMax", 64)
    var dprdeviationDpr = varchar("dprdeviationDpr", 64)
    var dprhz = varchar("dprhz", 64)
    var dprresult = varchar("dprresult", 64)
}

class Protocol(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Protocol>(ProtocolsTable)

    //DATA//
    var objectName by ProtocolsTable.objectName
    var dataType by ProtocolsTable.type
    var date by ProtocolsTable.date
    var dateManufacture by ProtocolsTable.dateManufacture
    var time by ProtocolsTable.time
    var operator by ProtocolsTable.operator
    var serial by ProtocolsTable.serial

    var dataP by ProtocolsTable.dataP
    var dataU by ProtocolsTable.dataU
    var dataN by ProtocolsTable.dataN
    var dataF by ProtocolsTable.dataF
    var dataKPD by ProtocolsTable.dataKPD
    var dataCOS by ProtocolsTable.dataCOS

    //MGR//
    var mgrU by ProtocolsTable.mgrU
    var mgrR15 by ProtocolsTable.mgrR15
    var mgrR60 by ProtocolsTable.mgrR60
    var mgrkABS by ProtocolsTable.mgrkABS
    var mgrResult by ProtocolsTable.mgrResult

    //IKAS//
    var ikasRA by ProtocolsTable.ikasRA
    var ikasRB by ProtocolsTable.ikasRB
    var ikasRC by ProtocolsTable.ikasRC
    var ikasDeviation by ProtocolsTable.ikasDeviation
    var ikasResult by ProtocolsTable.ikasResult

    //MOMENT//
    var momentN by ProtocolsTable.momentN
    var momentAVG by ProtocolsTable.momentAVG
    var momentMAX by ProtocolsTable.momentMAX
    var momentDeviation by ProtocolsTable.momentDeviation
    var momentResult by ProtocolsTable.momentResult

    //VOLTAGE//
    var voltageF by ProtocolsTable.voltageF
    var voltageN by ProtocolsTable.voltageN
    var voltageUAB by ProtocolsTable.voltageUAB
    var voltageUBC by ProtocolsTable.voltageUBC
    var voltageUCA by ProtocolsTable.voltageUCA
    var voltageUAB1000 by ProtocolsTable.voltageUAB1000
    var voltageUBC1000 by ProtocolsTable.voltageUBC1000
    var voltageUCA1000 by ProtocolsTable.voltageUCA1000
    var voltageDeviation by ProtocolsTable.voltageDeviation
    var voltageResult by ProtocolsTable.voltageResult

    //DPR//

    var dprampSinP by ProtocolsTable.dprampSinP
    var dprampSinN by ProtocolsTable.dprampSinN
    var dprampCosP by ProtocolsTable.dprampCosP
    var dprampCosN by ProtocolsTable.dprampCosN
    var dprmaxSinP by ProtocolsTable.dprmaxSinP
    var dprmaxSinN by ProtocolsTable.dprmaxSinN
    var dprmaxCosP by ProtocolsTable.dprmaxCosP
    var dprmaxCosN by ProtocolsTable.dprmaxCosN
    var dprminSinP by ProtocolsTable.dprminSinP
    var dprminSinN by ProtocolsTable.dprminSinN
    var dprminCosP by ProtocolsTable.dprminCosP
    var dprminCosN by ProtocolsTable.dprminCosN
    var dprdprSinP by ProtocolsTable.dprdprSinP
    var dprdprSinN by ProtocolsTable.dprdprSinN
    var dprdprCosP by ProtocolsTable.dprdprCosP
    var dprdprCosN by ProtocolsTable.dprdprCosN
    var dprdeviationMin by ProtocolsTable.dprdeviationMin
    var dprdeviationMax by ProtocolsTable.dprdeviationMax
    var dprdeviationDpr by ProtocolsTable.dprdeviationDpr
    var dprhz by ProtocolsTable.dprhz
    var dprresult by ProtocolsTable.dprresult

    override fun toString(): String {
        return "$id"
    }
}
