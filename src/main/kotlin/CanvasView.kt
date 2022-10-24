import javafx.scene.Group
import javafx.scene.canvas.Canvas
import javafx.scene.control.Label
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Line
import javafx.scene.shape.Rectangle
import javafx.scene.shape.Shape


internal class CanvasView(
    private val model: Model
) : Pane(), IView {
    override fun updateView() {
        model.canvas.widthProperty().bind(this.widthProperty())
        model.canvas.heightProperty().bind(this.heightProperty())
        if(model.selectShape != null) {
            val cur = model.selectShape
            when (cur) {
                is Line -> {
                    model.text.x = (cur.endX + cur.startX) / 2
                    model.text.y = (cur.endY + cur.startY) / 2
                    model.text.isVisible = true
                    model.text.toFront()
                    model.lineColor = cur.stroke as Color?
                    model.linestyle = cur.style
                    model.strokeWidth = cur.strokeWidth
                }
                is Rectangle -> {
                    model.text.x = cur.x
                    model.text.y = cur.y
                    model.text.isVisible = true
                    model.text.toFront()
                    model.lineColor = cur.stroke as Color?
                    model.strokeWidth = cur.strokeWidth
                    model.linestyle = cur.style
                    model.fillColor = cur.fill as Color?
                }
                is Circle -> {
                    model.text.x = cur.centerX
                    model.text.y = cur.centerY - cur.radius
                    model.text.isVisible = true
                    model.text.toFront()
                    model.lineColor = cur.stroke as Color?
                    model.strokeWidth = cur.strokeWidth
                    model.linestyle = cur.style
                    model.fillColor = cur.fill as Color?
                }
            }
        }
        else {
            model.text.isVisible = false
        }
    }

    init {
        model.canvas.setOnMousePressed { event ->
            when (model.toolSelected) {
                "line" -> {
                    model.startLine(event.x, event.y)
                }
                "rectangle" -> {
                    model.startRect(event.x, event.y)
                }
                "circle" -> {
                    model.startCircle(event.x, event.y)
                }
                "selection" -> {
                    model.select(event.x, event.y)
                }
                "eraser" -> {
                    model.erase(event.x, event.y)
                }
                "paint" -> {
                    model.paint(event.x, event.y)
                }
            }
        }

        model.canvas.setOnMouseDragged { event ->
            when (model.toolSelected) {
                "line" -> {
                    model.endLine(event.x, event.y)
                }
                "rectangle" -> {
                    model.endRect(event.x, event.y)
                }
                "circle" -> {
                    model.endCircle(event.x, event.y)
                }
                "selection" -> {
                    model.drag(event.x, event.y)
                }
            }
        }

        model.canvas.setOnMouseReleased { event ->
            when (model.toolSelected) {
                "line" -> {
                    model.endLine(event.x, event.y)
                    model.canvas.toFront()
                }
                "rectangle" -> {
                    model.endRect(event.x, event.y)
                    model.canvas.toFront()
                }
                "circle" -> {
                    model.endCircle(event.x, event.y)
                    model.canvas.toFront()
                }
                "selection" -> {
                    model.select(event.x, event.y)
                    model.state = Model.STATE.NONE
                    model.canvas.toFront()
                }
            }
        }

        model.root.children.addAll(model.text, model.canvas)
        this.children.add(model.root)
        this.viewOrder = 1.0
        model.canvas.widthProperty().bind(this.widthProperty())
        model.canvas.heightProperty().bind(this.heightProperty())
        model.addView(this)
    }


}