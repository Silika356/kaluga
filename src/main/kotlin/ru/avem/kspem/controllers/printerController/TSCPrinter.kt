package ru.avem.kspem.controllers.printerController

import com.sun.jna.Library
import com.sun.jna.Native
import ru.avem.kstpl.dsl.TSPLDocument
import ru.avem.kstpl.parser.TSPLParser
import tornadofx.controlsfx.errorNotification

interface TSCPrinter : Library {
    companion object {
        fun initJNA() {
            System.setProperty("jna.library.path", "cfg")
        }

        fun printDocument(document: TSPLDocument) {
            val commands = TSPLParser(document).parse()
            INSTANCE.openport("TSC TE200")
            if (INSTANCE.usbportqueryprinter().toInt() != -1) {
                commands.forEach {
                    INSTANCE.sendcommand(it)
                }
            } else {
                errorNotification("Ошибка", "Принтер не подключен")
            }
            INSTANCE.closeport()
        }

        val INSTANCE: TSCPrinter by lazy { Native.load("TSCLIB", TSCPrinter::class.java) }
    }

    fun about(): Int
    fun openport(pirnterName: String?): Int
    fun closeport(): Int
    fun sendcommand(printerCommand: String?): Int
    fun sendBinaryData(printerCommand: ByteArray?, CommandLength: Int): Int
    fun setup(
        width: String?,
        height: String?,
        speed: String?,
        density: String?,
        sensor: String?,
        vertical: String?,
        offset: String?
    ): Int

    fun downloadpcx(filename: String?, image_name: String?): Int
    fun barcode(
        x: String?,
        y: String?,
        type: String?,
        height: String?,
        readable: String?,
        rotation: String?,
        narrow: String?,
        wide: String?,
        code: String?
    ): Int

    fun printerfont(
        x: String?,
        y: String?,
        fonttype: String?,
        rotation: String?,
        xmul: String?,
        ymul: String?,
        text: String?
    ): Int

    fun clearbuffer(): Int
    fun printlabel(set: String?, copy: String?): Int
    fun windowsfont(
        x: Int,
        y: Int,
        fontheight: Int,
        rotation: Int,
        fontstyle: Int,
        fontunderline: Int,
        szFaceName: String?,
        content: String?
    ): Int

    fun windowsfontUnicode(
        x: Int,
        y: Int,
        fontheight: Int,
        rotation: Int,
        fontstyle: Int,
        fontunderline: Int,
        szFaceName: String?,
        content: ByteArray?
    ): Int

    fun windowsfontUnicodeLengh(
        x: Int,
        y: Int,
        fontheight: Int,
        rotation: Int,
        fontstyle: Int,
        fontunderline: Int,
        szFaceName: String?,
        content: ByteArray?,
        length: Int
    ): Int

    fun usbportqueryprinter(): Byte
}
