package ru.avem.kspem.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.kspem.app.Main
import ru.avem.kspem.data.dprType
import ru.avem.kspem.database.entities.*
import ru.avem.kspem.database.entities.Users.login
import ru.avem.kspem.utils.copyFileFromStream
import java.io.File
import java.sql.Connection
import java.text.SimpleDateFormat

fun validateDB() {

    if (!File("cfg").exists()) {
        File("cfg").mkdir()
    }

    if (!File("cfg\\log.txt").exists()) {
        File("cfg\\log.txt").createNewFile()
        File("cfg\\log.txt").setWritable(true)
    } else if (File("cfg\\log.txt").length() > 200000) {
        File("cfg\\log.txt").delete()
        File("cfg\\log.txt").createNewFile()
        File("cfg\\log.txt").setWritable(true)
    }
    if (!File("cfg\\ReadMe.txt").exists()) {
        copyFileFromStream(Main::class.java.getResource("ReadMe.txt").openStream(), File("cfg/ReadMe.txt"))
    }
    if (!File("cfg\\protocol.xlsx").exists()) {
        copyFileFromStream(Main::class.java.getResource("protocol.xlsx").openStream(), File("cfg/protocol.xlsx"))
    }

    File("cfg\\log.txt").appendText("\n-/-/-/-/  Программа запущена  ${SimpleDateFormat("dd.MM.y").format(System.currentTimeMillis())}  /-/-/-/-")

    Database.connect("jdbc:sqlite:cfg\\data.db", "org.sqlite.JDBC")
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

    transaction {
        SchemaUtils.create(Users, ProtocolsTable, Objects)
    }

    transaction {
        val admin = User.find {
            login eq "admin"
        }

        if (admin.empty()) {
            User.new {
                login = "admin"
                password = "avem"
            }
        }

        if (TestObjects.all().count() < 1) {
            TestObjects.new {
                name = "ДСМ-0,18-300 цифровой"
                typeDpr = dprType.digital
                pNom = "0.18"
                uNom = "220"
                fNom = "50"
                nNom = "300"
                mNom = "5.7"
                kpd = "96"
                cos = "0.93"
            }

            TestObjects.new {
                name = "ДСМ-0,18-300 синусный"
                typeDpr = dprType.sin
                pNom = "0.18"
                uNom = "220"
                fNom = "50"
                nNom = "300"
                mNom = "5.7"
                kpd = "96"
                cos = "0.93"
            }

            Protocol.new {
                objectName = "ДСМ-0,18-300"
                dataType = dprType.sin
                date = SimpleDateFormat("dd.MM.y").format(System.currentTimeMillis()).toString()
                time = SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis()).toString()
                operator = "Авиаагрегат-Н"
                serial = "Не задан"
                dataP = "0.18"
                dataU = "220"
                dataN = "300"
                dataF = "50"
                dataKPD = "96"
                dataCOS = "0.93"

                mgrU = "1000"
                mgrR15 = "3000"
                mgrR60 = "5000"
                mgrkABS = "1.6"
                mgrResult = "Успешно"

                ikasRA = "0.03"
                ikasRB = "0.031"
                ikasRC = "0.03"
                ikasDeviation = "3.3"
                ikasResult = "Успешно"

                momentN = "60"
                momentAVG = "0.50"
                momentMAX = "0.51"
                momentDeviation = "2"
                momentResult = "Успешно"

                voltageN = "300"
                voltageF = "50"
                voltageUAB = "218"
                voltageUBC = "220"
                voltageUCA = "223"
                voltageUAB1000 = "726.6"
                voltageUBC1000 = "733.3"
                voltageUCA1000 = "743.26"
                voltageDeviation = "2.3"
                voltageResult = "Успешно"


            }
        }
    }
}
