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
    val mNom = varchar("mNom", 128)
    val nNom = varchar("nNom", 128)
    val kpd = varchar("kpd", 128)
    val cos = varchar("cos", 128)
}

class TestObjects(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TestObjects>(Objects)

    var name by Objects.name
    var typeDpr by Objects.dprType
    var pNom by Objects.pNom
    var uNom by Objects.uNom
    var fNom by Objects.hzNom
    var mNom by Objects.mNom
    var nNom by Objects.nNom
    var kpd by Objects.kpd
    var cos by Objects.cos

    override fun toString(): String {
        return name
    }
}
