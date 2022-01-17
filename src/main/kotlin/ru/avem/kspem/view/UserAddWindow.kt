package ru.avem.kspem.view

import javafx.geometry.Pos
import javafx.scene.control.TextField
import javafx.scene.paint.Color
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.kspem.database.entities.User
import ru.avem.kspem.utils.Singleton
import ru.avem.kspem.utils.callKeyBoard
import tornadofx.*
import tornadofx.controlsfx.warningNotification

class UserAddWindow : View("Добавить пользователя") {
    private val parentView: UserEditorWindow by inject()

    //    private var textFieldLogin: TextField by singleAssign()
    private var textFieldPassword: TextField by singleAssign()
    private var textFieldFullName: TextField by singleAssign()

    override fun onBeforeShow() {
        modalStage!!.setOnHiding {
            parentView.refreshUsersTable()
        }
    }

    override fun onDock() {
        root.style {
            baseColor = Color.hsb(Singleton.color1, Singleton.color2, Singleton.color3)
        }
        super.onDock()
    }

    override val root = anchorpane {
        vbox(spacing = 16.0) {
            prefWidth = 600.0

            anchorpaneConstraints {
                leftAnchor = 16.0
                rightAnchor = 16.0
                topAnchor = 16.0
                bottomAnchor = 16.0
            }

            alignmentProperty().set(Pos.CENTER)


            hbox(spacing = 16.0) {
                alignmentProperty().set(Pos.CENTER_RIGHT)

                hbox(spacing = 16.0) {
                    alignmentProperty().set(Pos.CENTER_RIGHT)

                    label("ФИО")
                    textFieldFullName = textfield {
                        prefWidth = 200.0

                        callKeyBoard()

                    }
                }
                label("Пароль")
                textFieldPassword = textfield {
                    prefWidth = 200.0

                    callKeyBoard()

                }
            }





            button("Добавить") {
                action {
//                    val userLogin = textFieldLogin.text
                    val userPassword = textFieldPassword.text
//                    val fullName = textFieldFullName.text
                    val userLogin = textFieldFullName.text
                    val userList = mutableListOf<String>()
                    transaction {
                        User.all().forEach {
                            userList.add(it.login)
                        }
                    }

                    when {
                        userLogin.isNullOrEmpty() -> {
                            warningNotification(
                                "Ошибка",
                                "Введите логин",
                                Pos.BOTTOM_CENTER
                            )
                        }
                        userList.contains(userLogin.capitalize()) -> {
                            warningNotification(
                                "Ошибка",
                                "Имя занято",
                                Pos.BOTTOM_CENTER
                            )
                        }
                        else -> {
                            transaction {
                                User.new {
                                    login = userLogin
                                    password = userPassword
                                }
                            }
                            parentView.refreshUsersTable()
                            textFieldFullName.clear()
                            textFieldPassword.clear()
                            //                        textFieldLogin.clear()
                            //                        textFieldPassword.clear()
                            this@UserAddWindow.close()
                        }
                    }
                }
            }
        }
    }.addClass(Styles.medium, Styles.mainTheme)
}
