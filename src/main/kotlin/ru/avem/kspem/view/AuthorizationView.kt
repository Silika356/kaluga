package ru.avem.kspem.view

import javafx.beans.property.SimpleStringProperty
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.paint.Color
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.kspem.controllers.MainViewController
import ru.avem.kspem.database.entities.User
import ru.avem.kspem.database.entities.Users
import ru.avem.kspem.database.entities.Users.login
import ru.avem.kspem.utils.Singleton
import tornadofx.*
import tornadofx.controlsfx.confirmNotification
import tornadofx.controlsfx.warningNotification
import java.awt.Desktop
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.system.exitProcess

class AuthorizationView : View("Авторизация") {
    private var loginProperty = SimpleStringProperty("")
    private val passwordProperty = SimpleStringProperty("")

    private lateinit var users: List<User>

    private var comboboxUser: ComboBox<User> by singleAssign()
    private var comboboxUser2: ComboBox<User> by singleAssign()
    private val mainController: MainViewController by inject()
    val view: MainView by inject()

    override val configPath: Path = Paths.get("cfg/app.properties")

    override fun onDock() {
        super.onDock()
//        modalStage!!.fullScreenExitKeyCombination = KeyCombination.NO_MATCH
//        modalStage!!.isFullScreen = true
//        modalStage!!.isMaximized = true
//        modalStage!!.isResizable = false

        root.style {
            baseColor = Color.hsb(Singleton.color1, Singleton.color2, Singleton.color3)
        }
        comboboxUser.items = transaction {
            User.all().toList().asObservable()
        }
        passwordProperty.value = ""
    }



    override val root = anchorpane {
        prefWidth = 1920.0
        prefHeight = 1080.0
        try {
            Singleton.color1 = config["COLOR"].toString().toDouble()
            Singleton.color2 = config["SATURATION"].toString().toDouble()
            Singleton.color3 = config["BRIGHTNESS"].toString().toDouble()
        } catch (e: Exception) {
            with(config) {
                set("COLOR" to 20)
                set("SATURATION" to 0.5)
                set("BRIGHTNESS" to 1.0)
                save()
                Singleton.color1 = config["COLOR"].toString().toDouble()
                Singleton.color2 = config["SATURATION"].toString().toDouble()
                Singleton.color3 = config["BRIGHTNESS"].toString().toDouble()
            }
        }

        vbox(spacing = 64.0) {
            anchorpaneConstraints {
                topAnchor = 0.0
                bottomAnchor = 0.0
                leftAnchor = 0.0
                rightAnchor = 0.0
            }
            paddingTop = 24
            alignmentProperty().set(Pos.CENTER)

            label("Авторизация") {
                style {
                    fontSize = 50.px
                }
            }

            hbox(spacing = 24.0) {
                alignment = Pos.CENTER
                label("ФИО   ") {
                }
                comboboxUser = combobox {
                    prefWidth = 400.0
                }
            }

            hbox(spacing = 16.0) {
                alignment = Pos.CENTER

                label("Пароль*")

                passwordfield {
                    prefWidth = 400.0

                    onTouchReleased = EventHandler {
                        runLater {
                            Desktop.getDesktop()
                                .open(
                                    Paths.get("C:/Program Files/Common Files/Microsoft Shared/ink/TabTip.exe").toFile()
                                )
                            requestFocus()
                        }
                    }
                    promptText = "Пароль"
                }.bind(passwordProperty)
            }
            label("* - если задан")
            hbox(spacing = 128) {
                alignment = Pos.CENTER
                button("Вход") {
                    isDefaultButton = true
                    anchorpaneConstraints {
                        leftAnchor = 16.0
                        rightAnchor = 16.0
                        topAnchor = 270.0
                    }

                    onAction = EventHandler {
                        loginProperty = SimpleStringProperty(comboboxUser.selectedItem?.login)
                        if (loginProperty.value.isNullOrEmpty()) {
                            runLater {
                                warningNotification(
                                    "Пустой логин или пароль",
                                    "Заполните все поля",
                                    Pos.BOTTOM_CENTER,
                                    hideAfter = 3.seconds
                                )
                            }
                            return@EventHandler
                        } else {
                            transaction {
                                users = User.find {
                                    (login eq loginProperty.value) and (Users.password eq passwordProperty.value)
                                }.toList()
                                if (users.isEmpty()) {
                                    runLater {
                                        warningNotification(
                                            "Неправильный логин или пароль",
                                            "Проверьте данные для входа и повторите снова.",
                                            Pos.BOTTOM_CENTER,
                                            hideAfter = 3.seconds
                                        )
                                    }
                                } else {
                                    mainController.position1 = loginProperty.value
//                                if (!comboboxUser2.selectionModel.isEmpty) {
//                                    mainController.position2 = comboboxUser2.selectionModel.selectedItem.login
//                                    position2 = comboboxUser2.selectionModel.selectedItem.login
//                                } else {
//                                    mainController.position2 = mainController.position1
//                                    position2 = mainController.position1
//                                }
                                    runLater {
                                        confirmNotification(
                                            "Авторизация",
                                            "Вы вошли как: ${loginProperty.value}",
                                            Pos.BOTTOM_CENTER
                                        )
                                    }
                                    replaceWith<MainView>()
                                    runLater {
                                        view.setToDefault()
//                                    view.vBoxLog.clear()
                                    }
                                }
                            }
                        }
                    }
                }
                button("Выход") {
                    action {
                        exitProcess(0)
                    }
                }
            }
//            label()
//            label("Второй оператор (если требуется)")
//            hbox(spacing = 24.0) {
//                alignment = Pos.CENTER
//                label("ФИО   ") {
//                }
//                comboboxUser2 = combobox {
//                    prefWidth = 400.0
//                }
//            }
//            button("Очистить") {
//                action {
//                    comboboxUser2.selectionModel.clearSelection()
//                }
//            }
        }
    }.addClass(Styles.mainTheme)
}