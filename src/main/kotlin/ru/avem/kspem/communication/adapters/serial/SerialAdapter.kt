package ru.avem.kspem.communication.adapters.serial

import mu.KotlinLogging
import ru.avem.kserialpooler.communication.Connection
import ru.avem.kserialpooler.communication.adapters.AdapterInterface
import ru.avem.kserialpooler.communication.utils.toHexString
import java.io.IOException

class SerialAdapter(override val connection: Connection) : AdapterInterface {
    fun write(outputArray: ByteArray): Int {
        var numBytesWrite = 0
        try {
            numBytesWrite = connection.write(outputArray)
            KotlinLogging.logger("SerialAdapter").info("Write $numBytesWrite bytes")
            KotlinLogging.logger("SerialAdapter").info("Write " + outputArray.toHexString())
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return numBytesWrite
    }

    fun read(inputArray: ByteArray): Int {
        var numBytesRead = 0
        try {
            numBytesRead = connection.read(inputArray)
            KotlinLogging.logger("SerialAdapter").info("Read $numBytesRead bytes")
            KotlinLogging.logger("SerialAdapter").info("Read: " + inputArray.toHexString(numBytesRead = numBytesRead))
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return numBytesRead
    }
}