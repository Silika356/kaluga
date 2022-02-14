package ru.avem.kspem.protocol

import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import ru.avem.kspem.app.Main
import ru.avem.kspem.database.entities.Protocol
import ru.avem.kspem.utils.Toast
import ru.avem.kspem.utils.copyFileFromStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException


fun saveProtocolAsWorkbook(protocol: Protocol, path: String = "cfg/lastOpened.xlsx") {
    val template = File(path)
//    copyFileFromStream(Main::class.java.getResource("protocol.xlsx").openStream(), template)
    if (File("cfg\\protocol.xlsx").exists()) {
        copyFileFromStream(File("cfg/protocol.xlsx").inputStream(), template)
    } else {
        copyFileFromStream(Main::class.java.getResource("protocol.xlsx").openStream(), File("cfg/protocol.xlsx"))
        copyFileFromStream(File("cfg/protocol.xlsx").inputStream(), template)
    }

    try {
        XSSFWorkbook(template).use { wb ->
            val sheet = wb.getSheetAt(0)
            for (iRow in 0 until 150) {
                val row = sheet.getRow(iRow)
                if (row != null) {
                    for (iCell in 0 until 150) {
                        val cell = row.getCell(iCell)
                        if (cell != null && (cell.cellType == CellType.STRING)) {
                            when (cell.stringCellValue) {
                                "#OBJECTNAME#" -> cell.setCellValue(protocol.objectName)
                                "#PROTOCOL_NUMBER#" -> cell.setCellValue(protocol.id.toString())
                                "#DATE#" -> cell.setCellValue(protocol.date)
                                "#TIME#" -> cell.setCellValue(protocol.time)
                                "#DATA_TYPE#" -> cell.setCellValue(protocol.dataType)
                                "#OPERATOR#" -> cell.setCellValue(protocol.operator)
                                "#SERIAL#" -> cell.setCellValue(protocol.serial)
                                "#DATA_P#" -> cell.setCellValue(protocol.dataP)
                                "#DATA_U#" -> cell.setCellValue(protocol.dataU)
                                "#DATA_F#" -> cell.setCellValue(protocol.dataF)
                                "#DATA_N#" -> cell.setCellValue(protocol.dataN)
                                "#DATA_KPD#" -> cell.setCellValue(protocol.dataKPD)
                                "#DATA_COS#" -> cell.setCellValue(protocol.dataCOS)
                                "#DATA_TYPE" -> cell.setCellValue(protocol.dataType)
                                //MGR
                                "#MGR_U#" -> cell.setCellValue(protocol.mgrU)
                                "#MGR_R15#" -> cell.setCellValue(protocol.mgrR15)
                                "#MGR_R60#" -> cell.setCellValue(protocol.mgrR60)
                                "#MGR_KABS#" -> cell.setCellValue(protocol.mgrkABS)
                                "#MGR_RESULT#" -> cell.setCellValue(protocol.mgrResult)
                                //IKAS
                                "#IKAS_RA#" -> cell.setCellValue(protocol.ikasRA)
                                "#IKAS_RB#" -> cell.setCellValue(protocol.ikasRB)
                                "#IKAS_RC#" -> cell.setCellValue(protocol.ikasRC)
                                "#IKAS_DEVIATION#" -> cell.setCellValue(protocol.ikasDeviation)
                                "#IKAS_RESULT#" -> cell.setCellValue(protocol.ikasResult)
                                //MOMENT
                                "#MOMENT_MAX#" -> cell.setCellValue(protocol.momentMAX)
                                "#MOMENT_N#" -> cell.setCellValue(protocol.momentN)
                                "#MOMENT_AVG#" -> cell.setCellValue(protocol.momentAVG)
                                "#MOMENT_DEVIATION#" -> cell.setCellValue(protocol.momentDeviation)
                                "#MOMENT_RESULT#" -> cell.setCellValue(protocol.momentResult)
                                //VOLTAGE
                                "#VOLTAGE_UAB#" -> cell.setCellValue(protocol.voltageUAB)
                                "#VOLTAGE_UBC#" -> cell.setCellValue(protocol.voltageUBC)
                                "#VOLTAGE_UCA#" -> cell.setCellValue(protocol.voltageUCA)
                                "#VOLTAGE_UAB1000#" -> cell.setCellValue(protocol.voltageUAB1000)
                                "#VOLTAGE_UBC1000#" -> cell.setCellValue(protocol.voltageUBC1000)
                                "#VOLTAGE_UCA1000#" -> cell.setCellValue(protocol.voltageUCA1000)
                                "#VOLTAGE_DEVIATION#" -> cell.setCellValue(protocol.voltageDeviation)
                                "#VOLTAGE_F#" -> cell.setCellValue(protocol.voltageF)
                                "#VOLTAGE_N#" -> cell.setCellValue(protocol.voltageN)
                                "#VOLTAGE_RESULT#" -> cell.setCellValue(protocol.voltageResult)
                                //DPR
                                "#DPR_UMINSINP#" -> cell.setCellValue(protocol.dprminSinP)
                                "#DPR_UMINCOSP#" -> cell.setCellValue(protocol.dprminCosP)
                                "#DPR_UMINSINN#" -> cell.setCellValue(protocol.dprminSinN)
                                "#DPR_UMINCOSN#" -> cell.setCellValue(protocol.dprminCosN)
                                "#DPR_UMAXSINP#" -> cell.setCellValue(protocol.dprmaxSinP)
                                "#DPR_UMAXCOSP#" -> cell.setCellValue(protocol.dprmaxCosP)
                                "#DPR_UMAXSINN#" -> cell.setCellValue(protocol.dprmaxSinN)
                                "#DPR_UMAXCOSN#" -> cell.setCellValue(protocol.dprmaxCosN)
                                "#DPR_UAMPSINP#" -> cell.setCellValue(protocol.dprampSinP)
                                "#DPR_UAMPCOSP#" -> cell.setCellValue(protocol.dprampCosP)
                                "#DPR_UAMPSINN#" -> cell.setCellValue(protocol.dprampSinN)
                                "#DPR_UAMPCOSN#" -> cell.setCellValue(protocol.dprampCosN)
                                "#DPR_DPRSINP#" -> cell.setCellValue(protocol.dprdprSinP)
                                "#DPR_DPRCOSP#" -> cell.setCellValue(protocol.dprdprCosP)
                                "#DPR_DPRSINN#" -> cell.setCellValue(protocol.dprdprSinN)
                                "#DPR_DPRCOSN#" -> cell.setCellValue(protocol.dprdprCosN)
                                "#DPR_DEVIATIONMIN#" -> cell.setCellValue(protocol.dprdeviationMin)
                                "#DPR_DEVIATIONMAX#" -> cell.setCellValue(protocol.dprdeviationMax)
                                "#DPR_DEVIATIONDPR#" -> cell.setCellValue(protocol.dprdeviationDpr)
                                "#DPR_RESULT#" -> cell.setCellValue(protocol.dprresult)
                                else -> {
                                    if (cell.stringCellValue.contains("#")) {
                                        cell.setCellValue("")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            val outStream = ByteArrayOutputStream()
            wb.write(outStream)
            outStream.close()
        }
    } catch (e: FileNotFoundException) {
        Toast.makeText("Не удалось сохранить протокол на диск")
    }
}


