package ru.avem.kspem.view

import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.ButtonType
import javafx.scene.control.TableView
import javafx.scene.paint.Color
import javafx.stage.Modality
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import ru.avem.kspem.controllers.MainViewController
import ru.avem.kspem.database.entities.User
import ru.avem.kspem.database.entities.Users
import ru.avem.kspem.database.entities.Users.login
import ru.avem.kspem.utils.Singleton
import tornadofx.*
import tornadofx.controlsfx.warningNotification

class UserEditorWindow : View("Редактор пользователей") {
    private var tableViewUsers: TableView<User> by singleAssign()
    val mainController: MainViewController by inject()

    override fun onBeforeShow() {
        modalStage!!.setOnHiding {
        }
    }

    override fun onDock() {
        root.style {
            baseColor = Color.hsb(Singleton.color1, Singleton.color2, Singleton.color3)
        }
        super.onDock()
    }

    fun refreshUsersTable() {
        tableViewUsers.items = getUsers()
    }

    private fun getUsers(): ObservableList<User> {
        return transaction {
              User.all().toList().asObservable()
//                  .filter {it.login != "admin"}
        }
    }

    override val root = anchorpane {
        hbox(spacing = 16.0) {
            anchorpaneConstraints {
                leftAnchor = 16.0
                rightAnchor = 16.0
                bottomAnchor = 16.0
                topAnchor = 16.0
            }

            alignmentProperty().set(Pos.CENTER)

            tableViewUsers = tableview {
                minWidth = 1200.0
                minHeight = 800.0

                columnResizePolicyProperty().set(TableView.CONSTRAINED_RESIZE_POLICY)

                items = getUsers()

                column("ФИО", User::login) {
                    onEditCommit = EventHandler { cell ->
                        transaction {
                            Users.update({
                                login eq selectedItem!!.login
                            }) {
                                it[login] = cell.newValue
                            }
                        }
                    }
                }.isEditable = false
            }

            vbox(spacing = 16.0) {
                button("Добавить пользователя") {
                    prefWidth = 300.0
                    action {
                        find<UserAddWindow>().openModal(
                            modality = Modality.WINDOW_MODAL, escapeClosesWindow = true,
                            owner = this@UserEditorWindow.currentWindow, resizable = false
                        )
                    }
                }

                button("Удалить пользователя") {
                    prefWidth = 300.0

                    action {
                        val item = tableViewUsers.selectedItem
                        if (item != null) {
                            when (item.login) {
                                "admin" -> {
                                    warningNotification(
                                        "Удаление пользователя",
                                        "Нельзя удалить учетную запись администратора.",
                                        Pos.BOTTOM_CENTER
                                    )
                                }
                                mainController.position1 -> {
                                    warningNotification(
                                        "Удаление пользователя",
                                        "Это текущая учетная запись",
                                        Pos.BOTTOM_CENTER
                                    )
                                }
                                else -> {
                                    confirm(
                                        "Удаление пользователя ${item.login}",
                                        "Вы действительно хотите удалить пользователя?",
                                        ButtonType.YES, ButtonType.NO,
                                        owner = this@UserEditorWindow.currentWindow,
                                        title = "Удаление пользователя ${item.login}"
                                    ) {
                                        transaction {
                                            Users.deleteWhere { login eq item.login }
                                        }
                                        refreshUsersTable()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }.addClass(Styles.medium, Styles.mainTheme)
}
