import javafx.application.Platform
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent

internal class MenubarView (
    private val model: Model,
) : MenuBar(), IView {
    override fun updateView() {

    }

    init {
        val fileMenu = Menu("File")
        val newFile = MenuItem("New")
        val loadFile = MenuItem("Load")
        val saveFile = MenuItem("Save")
        val quitFile = MenuItem("Quit")
        fileMenu.items.addAll(newFile, loadFile, saveFile, quitFile)

        val editMenu = Menu("Edit")
        val cutEdit = MenuItem("Cut")
        val copyEdit = MenuItem("Copy")
        val pasteEdit = MenuItem("Paste")
        editMenu.items.addAll(cutEdit, copyEdit, pasteEdit)

        val helpMenu = Menu("Help")
        val aboutHelp = MenuItem("About")
        helpMenu.items.add(aboutHelp)

        newFile.setOnAction { model.newFile() }

        saveFile.setOnAction { model.saveFile() }

        loadFile.setOnAction { model.loadFile() }

        quitFile.setOnAction { Platform.exit() }

        aboutHelp.setOnAction { model.showAbout() }

        cutEdit.setOnAction { model.cut() }

        copyEdit.setOnAction { model.copy() }

        pasteEdit.setOnAction { model.paste() }

        this.menus.addAll(fileMenu, editMenu, helpMenu)

        model.addView(this)
    }

}