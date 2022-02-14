package ru.avem.kspem.database.entities

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object Objects : IntIdTable() {
    val name = varchar("name", 128)
    val dprType = varchar("dprType", 128)
    val pNom = varchar("pNom", 128)
    val uNom = varchar("uNom", 128)
    val hzNom = varchar("hzNom", 128)
    val iNom = varchar("iNom", 128)
    val weightNom = varchar("weightNom", 128)
    val mNom = varchar("mNom", 128)
    val nNom = varchar("nNom", 128)
    val kpd = varchar("kpd", 128)

    val uMGR = varchar("uMGR", 128)
    val kABS = varchar("kABS", 128)
    val ikasDev = varchar("IkasDev", 128)
    val voltageDev = varchar("VoltageDev", 128)
    val dprDev = varchar("DPRDev", 128)
    val dprDevDegree = varchar("dprDevDegree", 128)
    val momentMax = varchar("momentMax", 128)
    val dprPairs = varchar("dprPairs", 128)
}

class TestObjects(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TestObjects>(Objects)

    var name by Objects.name
    var typeDpr by Objects.dprType
    var pNom by Objects.pNom
    var uNom by Objects.uNom
    var hzNom by Objects.hzNom
    var iNom by Objects.iNom
    var weightNom by Objects.weightNom
    var mNom by Objects.mNom
    var nNom by Objects.nNom
    var kpd by Objects.kpd

    var uMGR by Objects.uMGR
    var kABS by Objects.kABS
    var ikasDev by Objects.ikasDev
    var voltageDev by Objects.voltageDev
    var dprDev by Objects.dprDev
    var momentMax by Objects.momentMax
    var dprDevDegree by Objects.dprDevDegree
    var dprPairs by Objects.dprPairs

    override fun toString(): String {
        return name
    }
}
