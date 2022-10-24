import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.stage.Stage

class Sketchit: Application() {
    override fun start(stage: Stage) {
        stage.title = "Sketchit"

        // create and initialize the Model to hold our counter
        val model = Model()

        val menubar = MenubarView(model)
        val toolbar = ToolbarView(model)
        val canvas = CanvasView(model)
        val pane = BorderPane()
        pane.top = menubar
        pane.left = toolbar
        pane.center = canvas

        // Add grid to a scene (and the scene to the stage)
        val scene = Scene(pane, 640.0, 480.0)
        stage.minWidth = 640.0
        stage.minHeight = 480.0
        stage.maxHeight = 1200.0
        stage.maxWidth = 1600.0
        stage.scene = scene
        stage.show()
    }
}