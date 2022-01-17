package ru.avem.kspem.communication.adapters.stringascii

import ru.avem.kserialpooler.communication.Connection
import ru.avem.kserialpooler.communication.adapters.AdapterInterface
import java.nio.charset.StandardCharsets

class StringASCIIAdapter(override val connection: Connection) : AdapterInterface {
    fun read(request: String): String {
        val requestNew = request + "\n"
        val requestByte = requestNew.toByteArray(StandardCharsets.US_ASCII)

        connection.write(requestByte, requestByte.size.toLong())

        val inputBytes = ByteArray(255)
        val readBytes = connection.read(inputBytes, inputBytes.size.toLong())
        return if (readBytes > 0) String(inputBytes) else ""
    }

    fun write(request: String) {
        val newRequest = request + "\n"
        val requestByte = newRequest.toByteArray(StandardCharsets.US_ASCII)
        connection.write(requestByte)
    }
}
