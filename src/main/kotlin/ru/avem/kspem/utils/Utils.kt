package ru.avem.kspem.utils


import javafx.event.EventHandler
import javafx.scene.control.ButtonType
import javafx.scene.control.TextField
import javafx.stage.Window
import ru.avem.kspem.view.Styles
import tornadofx.*
import java.awt.Desktop
import java.io.*
import java.nio.file.Paths
import java.util.*
import kotlin.math.abs

fun sleep(mills: Long) {
    Thread.sleep(mills)
}

fun formatRealNumber(num: Double): Double {
    val absNum = abs(num)

    var format = "%.0f"
    when {
        absNum == 0.0 -> format = "%.0f"
        absNum < 0.1f -> format = "%.5f"
        absNum < 1f -> format = "%.4f"
        absNum < 10f -> format = "%.3f"
        absNum < 100f -> format = "%.2f"
        absNum < 1000f -> format = "%.1f"
        absNum < 10000f -> format = "%.0f"
    }
    return String.format(Locale.US, format, num).toDouble()
}

fun openFile(file: File) {
    try {
        Desktop.getDesktop().open(file)
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun copyFileFromStream(_inputStream: InputStream, dest: File) {
    _inputStream.use { inputStream ->
        try {
            val fileOutputStream = FileOutputStream(dest)
            val buffer = ByteArray(1024)
            var length = inputStream.read(buffer)
            while (length > 0) {
                fileOutputStream.write(buffer, 0, length)
                length = inputStream.read(buffer)
            }
        } catch (e: FileNotFoundException) {
        }
    }
}


fun TextField.callKeyBoard() {
    onTouchPressed = EventHandler {
        Desktop.getDesktop()
            .open(Paths.get("C:/Program Files/Common Files/Microsoft Shared/ink/TabTip.exe").toFile())
        requestFocus()
    }
}



fun showTwoWayDialog(
    title: String,
    text: String,
    way1Title: String,
    way2Title: String,
    way1: () -> Unit,
    way2: () -> Unit,
    currentWindow: Window
) {
    runLater {
        warning(
            title,
            text,
            ButtonType(way1Title),
            ButtonType(way2Title),
            owner = currentWindow
        ) { buttonType ->
            when (buttonType.text) {
                way1Title -> way1()
                way2Title -> way2()
            }
        }
    }

}