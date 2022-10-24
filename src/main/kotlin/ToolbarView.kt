import javafx.geometry.Insets
import javafx.scene.control.ColorPicker
import javafx.scene.control.Label
import javafx.scene.control.ToggleButton
import javafx.scene.control.ToggleGroup
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import java.io.File

internal class ToolbarView (
    private val model: Model
) : BorderPane(), IView {
    override fun updateView() {
        val toolBox = this.children[0] as VBox
        val colorBox = toolBox.children[1] as VBox
        val thickBox = toolBox.children[2] as HBox
        val styleBox = toolBox.children[3] as HBox
        (colorBox.children[1] as ColorPicker).value = model.lineColor
        (colorBox.children[3] as ColorPicker).value = model.fillColor
        when (model.strokeWidth) {
            2.0 -> (thickBox.children[0] as ToggleButton).isSelected = true
            5.0 -> (thickBox.children[1] as ToggleButton).isSelected = true
            8.0 -> (thickBox.children[2] as ToggleButton).isSelected = true
        }
        when (model.linestyle) {
            "" -> {
                (styleBox.children[0] as ToggleButton).isSelected = true
            }
            "-fx-stroke-dash-array: 2 12 2 12;" -> {
                (styleBox.children[1] as ToggleButton).isSelected = true
            }
            "-fx-stroke-dash-array: 6 10 6 10;" -> {
                (styleBox.children[2] as ToggleButton).isSelected = true
            }
        }
    }

    init {
        val toolbox = VBox()
        val drawBox = VBox()
        val thickBox = HBox()
        val styleBox = HBox()
        val drawBox1 = HBox()
        val drawBox2 = HBox()
        val drawBox3 = HBox()

        val toolGroup = ToggleGroup()
        val selectionButton = ToggleButton()
        val eraserButton = ToggleButton()
        val lineButton = ToggleButton()
        val rectangleButton = ToggleButton()
        val circleButton = ToggleButton()
        val paintButton = ToggleButton()

        val cpLine = ColorPicker(Color.BLACK)
        val cpFill = ColorPicker(Color.BLACK)
        val lineLable = Label("Line Color")
        val fillLabel = Label("Fill Color")

        val linewidthGroup = ToggleGroup()
        val linewidth1 = ToggleButton()
        val linewidth2 = ToggleButton()
        val linewidth3 = ToggleButton()

        val linestyleGroup = ToggleGroup()
        val linestyle1 = ToggleButton()
        val linestyle2 = ToggleButton()
        val linestyle3 = ToggleButton()

        cpLine.style = "-fx-color-label-visible: false;"
        cpLine.minWidth = 90.0
        cpFill.style = "-fx-color-label-visible: false;"
        cpFill.minWidth = 90.0
        val colorBox = VBox()
        colorBox.children.addAll(lineLable, cpLine, fillLabel, cpFill)
        colorBox.spacing = 5.0
        colorBox.padding = Insets(10.0, 5.0, 5.0, 5.0)
        thickBox.spacing = 5.0
        thickBox.padding = Insets(10.0, 5.0, 5.0, 5.0)
        styleBox.spacing = 5.0
        styleBox.padding = Insets(10.0, 5.0, 5.0, 5.0)
        drawBox1.spacing = 5.0
        drawBox1.padding = Insets(5.0, 5.0, 5.0, 5.0)
        drawBox2.spacing = 5.0
        drawBox2.padding = Insets(5.0, 5.0, 5.0, 5.0)
        drawBox3.spacing = 5.0
        drawBox3.padding = Insets(5.0, 5.0, 5.0, 5.0)

        val dir = File("${System.getProperty("user.dir")}/src/main/resources")
        selectionButton.graphic = ImageView(Image(dir.toString().plus("/selection.png"), 25.0, 25.0, false, false))
        eraserButton.graphic = ImageView(Image(dir.toString().plus("/eraser.png"), 25.0, 25.0, false, false))
        lineButton.graphic = ImageView(Image(dir.toString().plus("/line.png"), 25.0, 25.0, false, false))
        rectangleButton.graphic = ImageView(Image(dir.toString().plus("/rectangle.png"), 25.0, 25.0, false, false))
        circleButton.graphic = ImageView(Image(dir.toString().plus("/circle.png"), 25.0, 25.0, false, false))
        paintButton.graphic = ImageView(Image(dir.toString().plus("/paint.png"), 25.0, 25.0, false, false))
        linewidth1.graphic = ImageView(Image(dir.toString().plus("/line1.png"), 25.0, 25.0, false, false))
        linewidth2.graphic = ImageView(Image(dir.toString().plus("/line2.png"), 25.0, 25.0, false, false))
        linewidth3.graphic = ImageView(Image(dir.toString().plus("/line3.png"), 25.0, 25.0, false, false))
        linestyle1.graphic = ImageView(Image(dir.toString().plus("/line.png"), 25.0, 25.0, false, false))
        linestyle2.graphic = ImageView(Image(dir.toString().plus("/dotline.png"), 25.0, 25.0, false, false))
        linestyle3.graphic = ImageView(Image(dir.toString().plus("/dashline.png"), 25.0, 25.0, false, false))

        selectionButton.setOnMouseClicked {   model.selectTool() }
        eraserButton.setOnMouseClicked { model.eraserTool() }
        lineButton.setOnMouseClicked { model.lineTool() }
        rectangleButton.setOnMouseClicked { model.rectTool() }
        circleButton.setOnMouseClicked { model.circleTool() }
        paintButton.setOnMouseClicked { model.paintTool() }
        cpLine.setOnAction { model.changeline(cpLine.value) }
        cpFill.setOnAction { model.changefill(cpFill.value) }
        linewidth1.setOnMouseClicked { model.changewidth(2.0) }
        linewidth2.setOnMouseClicked { model.changewidth(5.0) }
        linewidth3.setOnMouseClicked { model.changewidth(8.0) }
        linestyle1.setOnMouseClicked { model.changestyle("") }
        linestyle2.setOnMouseClicked { model.changestyle("-fx-stroke-dash-array: 2 12 2 12;")}
        linestyle3.setOnMouseClicked { model.changestyle("-fx-stroke-dash-array: 6 10 6 10;") }

        linewidthGroup.toggles.addAll(linewidth1, linewidth2, linewidth3)
        linestyleGroup.toggles.addAll(linestyle1, linestyle2, linestyle3)
        thickBox.children.addAll(linewidth1, linewidth2, linewidth3)
        styleBox.children.addAll(linestyle1, linestyle2, linestyle3)
        toolGroup.toggles.addAll(selectionButton, eraserButton, lineButton, rectangleButton, circleButton, paintButton)
        drawBox1.children.addAll(selectionButton, eraserButton)
        drawBox2.children.addAll(rectangleButton, circleButton)
        drawBox3.children.addAll(lineButton, paintButton)
        drawBox.children.addAll(drawBox1, drawBox2, drawBox3)
        drawBox.spacing = 5.0
        drawBox.padding = Insets(5.0, 5.0, 10.0, 5.0)
        toolbox.children.addAll(drawBox, colorBox, thickBox, styleBox)
        this.children.add(toolbox)
        this.prefWidth = 150.0
        this.style = "-fx-border-width: 0 1px 0 0; -fx-border-color: black; -fx-border-style: solid; -fx-background-color: white"

        this.addEventHandler(KeyEvent.KEY_PRESSED) { event ->
            when(event.code) {
                KeyCode.DELETE -> model.remove()
                KeyCode.ESCAPE -> {
                    model.escapeKey()
                }
            }
        }
        model.addView(this)
    }
}