package ru.avem.kspem.view

import javafx.application.Platform
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.kspem.database.entities.Protocol
import ru.avem.kspem.database.entities.ProtocolsTable
import ru.avem.kspem.protocol.saveProtocolAsWorkbook
import ru.avem.kspem.utils.Singleton
import ru.avem.kspem.utils.openFile
import tornadofx.*
import tornadofx.controlsfx.confirmNotification
import java.io.File

class ProtocolListWindow : View("Список протоколов испытаний") {
    private var tableViewProtocols: TableView<Protocol> by singleAssign()
    private lateinit var protocols: ObservableList<Protocol>
    override fun onDock() {
        root.style {
            baseColor = Color.hsb(Singleton.color1, Singleton.color2, Singleton.color3)
        }
        protocols = transaction {
            Protocol.all().toList().reversed().asObservable()
        }
        tableViewProtocols.items = protocols
        filter.text = ""
    }

    var filter: TextField by singleAssign()

    override val root = anchorpane {
        prefWidth = 1400.0
        prefHeight = 800.0

        vbox(spacing = 16.0) {
            anchorpaneConstraints {
                leftAnchor = 16.0
                rightAnchor = 16.0
                topAnchor = 16.0
                bottomAnchor = 16.0
            }

            alignmentProperty().set(Pos.CENTER)

            filter = textfield {
                prefWidth = 600.0

                promptText = "Фильтр"
                alignment = Pos.CENTER

                onKeyReleased = EventHandler {
                    if (!text.isNullOrEmpty()) {
                        tableViewProtocols.items = protocols.filter {
                            it.date.contains(text)
                                    || it.serial.contains(text)
                                    || it.operator.contains(text)
                                    || it.time.contains(text)
                                    || it.objectName.contains(text)
                        }.asObservable()
                    } else {
                        tableViewProtocols.items = protocols
                    }
                }
            }

            tableViewProtocols = tableview {
                protocols = transaction {
                    Protocol.all().toList().asObservable()
                }
                items = protocols
                prefHeight = 900.0
                minWidth = 1600.0
                columnResizePolicyProperty().set(TableView.CONSTRAINED_RESIZE_POLICY)
                column("Шифр двигателя", Protocol::objectName)
                column("Серийный номер", Protocol::serial)
                column("Оператор", Protocol::operator)
                column("Дата", Protocol::date)
                column("Время", Protocol::time)
            }

            hbox(spacing = 16.0) {
                alignmentProperty().set(Pos.CENTER)

                button("Открыть") {
                    action {
                        if (tableViewProtocols.selectedItem != null) {
                            Singleton.currentProtocol = transaction {
                                Protocol.find {
                                    ProtocolsTable.id eq tableViewProtocols.selectedItem!!.id
                                }.toList().asObservable()
                            }.first()
                            saveProtocolAsWorkbook(Singleton.currentProtocol)
                            openFile(File("cfg/lastOpened.xlsx"))
                            close()
                        }
                    }
                }
                button("Сохранить выбранный") {
                    action {
                        if (tableViewProtocols.selectedItem != null) {
                            val files = chooseFile(
                                "Выберите директорию для сохранения",
                                arrayOf(FileChooser.ExtensionFilter("XSLX Files (*.xlsx)", "*.xlsx")),
                                FileChooserMode.Save,
                                this@ProtocolListWindow.currentWindow
                            ) {
                                this.initialDirectory = File(System.getProperty("user.home")+"/Desktop")
                            }

                            if (files.isNotEmpty()) {
                                saveProtocolAsWorkbook(tableViewProtocols.selectedItem!!, files.first().absolutePath)

                                Platform.runLater {
                                    confirmNotification(
                                        "Готово",
                                        "Успешно сохранено",
                                        Pos.BOTTOM_CENTER,
                                        owner = this@ProtocolListWindow.currentWindow
                                    )
                                }
                            }
                        }
                    }
                }
                button("Сохранить все") {
                    action {
                        if (tableViewProtocols.items.size > 0) {
                            val dir = chooseDirectory(
                                "Выберите директорию для сохранения",
                                File(System.getProperty("user.home")+"/Desktop"),
                                this@ProtocolListWindow.currentWindow
                            )

                            if (dir != null) {
                                tableViewProtocols.items.forEach {
                                    val time: String = it.time.replace(":", "-")
                                    val file = File(dir, "name${it.objectName}_${it.date}_${time}_${it.serial}_${it.id}.xlsx")
                                    saveProtocolAsWorkbook(it, file.absolutePath)
                                }
                                Platform.runLater {
                                    confirmNotification(
                                        "Готово",
                                        "Успешно сохранено",
                                        Pos.BOTTOM_CENTER,
                                        owner = this@ProtocolListWindow.currentWindow
                                    )
                                }
                            }
                        }
                    }
                }
                button("Удалить") {
                    action {
                        if (tableViewProtocols.selectedItem != null) {
                            transaction {
                                ProtocolsTable.deleteWhere {
                                    ProtocolsTable.id eq tableViewProtocols.selectedItem!!.id
                                }
                            }

                            tableViewProtocols.items = transaction {
                                Protocol.all().toList().reversed().asObservable()
                            }
                        }
                    }
                }
            }
        }
    }.addClass(Styles.mainTheme)
}
