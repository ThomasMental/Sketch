import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.canvas.Canvas
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.control.Label
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.Circle
import javafx.scene.shape.Line
import javafx.scene.shape.Rectangle
import javafx.scene.shape.Shape
import javafx.scene.text.Text
import javafx.stage.FileChooser
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class Model {
    private val views: ArrayList<IView> = ArrayList()

    fun addView(view: IView) {
        views.add(view)
        view.updateView()
    }

    private fun notifyObservers() {
        for (view in views) {
            view.updateView()
        }
    }

    val root = Group()
    var canvas = Canvas(400.0, 400.0)
    var line = Line()
    var rect = Rectangle()
    var circle = Circle()
    var toolSelected = ""
    var lineColor = Color.BLACK
    var fillColor = Color.BLACK
    var linestyle = ""
    var strokeWidth = 2.0
    var saved = false
    var selectShape:Shape? = null
    var startX = -1.0
    var startY = -1.0
    enum class STATE { NONE, DRAG }
    var state = STATE.NONE
    var copyShape:Shape? = null
    var iscut = false
    var text = Text("Selected")

    fun closestPoint(M: Point2D, P0: Point2D, P1: Point2D): Point2D {
        val v = P1.subtract(P0) // v = P1 - P0
        // early out if line is less than 1 pixel long
        if (v.magnitude() < 1.0) return P0
        val u = M.subtract(P0) // u = M - P0
        // scalar of vector projection ...
        val s = u.dotProduct(v) / v.dotProduct(v)
        // find point for constrained line segment
        if (s < 0) return P0
        else if (s > 1) return P1
        else {
            val w = v.multiply(s) // w = s * v
            return P0.add(w) // Q = P0 + w
        }
    }

    fun selectTool() {
        toolSelected = "selection"
        selectShape = null
        notifyObservers()
    }

    fun eraserTool() {
        toolSelected = "eraser"
        selectShape = null
        notifyObservers()
    }

    fun lineTool() {
        toolSelected = "line"
        selectShape = null
        notifyObservers()
    }

    fun rectTool() {
        toolSelected = "rectangle"
        selectShape = null
        notifyObservers()
    }

    fun circleTool() {
        toolSelected = "circle"
        selectShape = null
        notifyObservers()
    }

    fun paintTool() {
        toolSelected = "paint"
        selectShape = null
        notifyObservers()
    }

    fun newFile() {
        var confirm = false
        if(!saved) {
            val confirmPrompt = Alert(Alert.AlertType.CONFIRMATION)
            confirmPrompt.title = "Confirmation"
            confirmPrompt.contentText = "File not saved. Do you want to continue?"
            val result = confirmPrompt.showAndWait()
            if (result.isPresent) {
                when (result.get()) {
                    ButtonType.OK -> {
                        confirm = true
                    }
                    ButtonType.CANCEL -> {
                        confirmPrompt.close()
                    }
                }
            }
        }
        else {
            confirm = true
        }

        if(confirm) {
            saved = false
            selectShape = null
            root.children.clear()
            root.children.addAll(text, canvas)
        }
        notifyObservers()
    }

    fun showAbout() {
        val dialog = Dialog<String>()
        dialog.title = "About"
        dialog.contentText = "Application Name: Sketch it\nDeveloper: Taochen Zhu\nStudent Number: 20871099\n"
        val closeButton = ButtonType("OK", ButtonBar.ButtonData.OK_DONE)
        dialog.dialogPane.buttonTypes.add(closeButton)
        dialog.showAndWait()

        notifyObservers()
    }

    fun saveFile() {
        saved = true
        val saveFile = FileChooser()
        saveFile.title = "Save File"
        val file = saveFile.showSaveDialog(null)

        if(file != null) {
            val filestream = FileOutputStream(file)
            val objstream = ObjectOutputStream(filestream)
            objstream.writeInt(root.children.size - 2)
            for(shape in root.children) {
                when (shape) {
                    is Line -> {
                        objstream.writeObject("Line")
                        objstream.writeObject(shape.stroke.toString())
                        objstream.writeObject(shape.style.toString())
                        objstream.writeDouble(shape.strokeWidth)
                        objstream.writeDouble(shape.startX)
                        objstream.writeDouble(shape.startY)
                        objstream.writeDouble(shape.endX)
                        objstream.writeDouble(shape.endY)
                    }
                    is Rectangle -> {
                        objstream.writeObject("Rectangle")
                        objstream.writeObject(shape.stroke.toString())
                        objstream.writeObject(shape.fill.toString())
                        objstream.writeObject(shape.style.toString())
                        objstream.writeDouble(shape.strokeWidth)
                        objstream.writeDouble(shape.x)
                        objstream.writeDouble(shape.y)
                        objstream.writeDouble(shape.width)
                        objstream.writeDouble(shape.height)
                    }
                    is Circle -> {
                        objstream.writeObject("Circle")
                        objstream.writeObject(shape.stroke.toString())
                        objstream.writeObject(shape.fill.toString())
                        objstream.writeObject(shape.style.toString())
                        objstream.writeDouble(shape.strokeWidth)
                        objstream.writeDouble(shape.centerX)
                        objstream.writeDouble(shape.centerY)
                        objstream.writeDouble(shape.radius)
                    }
                }
            }
            objstream.close()
            filestream.close()
        }
        notifyObservers()
    }

    fun loadFile() {
        var confirm = false
        if(!saved) {
            val confirmPrompt = Alert(Alert.AlertType.CONFIRMATION)
            confirmPrompt.title = "Confirmation"
            confirmPrompt.contentText = "File not saved. Do you want to continue?"
            val result = confirmPrompt.showAndWait()
            if (result.isPresent) {
                when (result.get()) {
                    ButtonType.OK -> {
                        confirm = true
                    }
                    ButtonType.CANCEL -> {
                        confirmPrompt.close()
                    }
                }
            }
        }
        else {
            confirm = true
        }

        if(confirm) {
            val loadFile = FileChooser()
            loadFile.title = "Open File"
            val file = loadFile.showOpenDialog(null)
            if (file != null) {
                saved = false
                selectShape = null
                root.children.clear()
                root.children.addAll(text, canvas)
                val filestream = FileInputStream(file)
                val objstream = ObjectInputStream(filestream)
                val number = objstream.readInt()
                for(num in 1..number) {
                    val shape = objstream.readObject()
                    if (shape == "Line") {
                        line = Line()
                        line.stroke = Paint.valueOf(objstream.readObject() as String?)
                        line.style = objstream.readObject() as String
                        line.strokeWidth = objstream.readDouble()
                        line.startX = objstream.readDouble()
                        line.startY = objstream.readDouble()
                        line.endX = objstream.readDouble()
                        line.endY = objstream.readDouble()
                        root.children.add(line)
                    }
                    else if(shape == "Rectangle") {
                        rect = Rectangle()
                        rect.stroke = Paint.valueOf(objstream.readObject() as String?)
                        rect.fill = Paint.valueOf(objstream.readObject() as String?)
                        rect.style = objstream.readObject() as String
                        rect.strokeWidth = objstream.readDouble()
                        rect.x = objstream.readDouble()
                        rect.y = objstream.readDouble()
                        rect.width = objstream.readDouble()
                        rect.height = objstream.readDouble()
                        root.children.add(rect)
                    }
                    else if(shape == "Circle") {
                        circle = Circle()
                        circle.stroke = Paint.valueOf(objstream.readObject() as String?)
                        circle.fill = Paint.valueOf(objstream.readObject() as String?)
                        circle.style = objstream.readObject() as String
                        circle.strokeWidth = objstream.readDouble()
                        circle.centerX = objstream.readDouble()
                        circle.centerY = objstream.readDouble()
                        circle.radius = objstream.readDouble()
                        root.children.add(circle)
                    }
                }
                canvas.toFront()
            }
        }
        notifyObservers()
    }

    fun startLine(x: Double, y: Double) {
        selectShape = null
        line = Line()
        line.stroke = lineColor
        line.strokeWidth = strokeWidth
        line.style = linestyle
        line.startX = x
        line.startY = y
        line.endX = x
        line.endY = y
        root.children.add(line)
        notifyObservers()
    }

    fun endLine(x: Double, y: Double) {
        line.endX = x
        line.endY = y
        notifyObservers()
    }

    fun startRect(x: Double, y: Double) {
        selectShape = null
        rect = Rectangle()
        rect.x = x
        rect.y = y
        rect.style = linestyle
        rect.width = 1.0
        rect.height = 1.0
        rect.fill = fillColor
        rect.stroke = lineColor
        rect.strokeWidth = strokeWidth
        root.children.add(rect)
        notifyObservers()
    }

    fun endRect(x: Double, y: Double) {
        rect.width = x - (rect.x ?:0.0)
        rect.height = y - (rect.y?:0.0)
        notifyObservers()
    }

    fun startCircle(x: Double, y: Double) {
        selectShape = null
        circle = Circle()
        circle.style = linestyle
        circle.centerX = x
        circle.centerY = y
        circle.fill = fillColor
        circle.stroke = lineColor
        circle.strokeWidth = strokeWidth
        root.children.add(circle)
        notifyObservers()
    }

    fun endCircle(x: Double, y: Double) {
        circle.radius = Point2D(x,y).distance(Point2D(circle.centerX, circle.centerY))
        notifyObservers()
    }


    fun select(x: Double, y: Double) {
        var findShape = false
        for(shape in root.children) {
            if(shape is Line) {
                val q = closestPoint(Point2D(x, y), Point2D(shape.startX, shape.startY), Point2D(shape.endX, shape.endY))
                if(Point2D(x, y).distance(q) <= shape.strokeWidth / 2) {
                    selectShape = shape
                    findShape = true
                }
            }
            else if(shape is Rectangle) {
                if(x >= shape.x && x <= shape.x + shape.width && y >= shape.y && y <= shape.y + shape.height) {
                    selectShape = shape
                    findShape = true
                }
            }
            else if(shape is Circle) {
                if(Point2D(x, y).distance(Point2D(shape.centerX, shape.centerY)) <= shape.radius) {
                    selectShape = shape
                    findShape = true
                }
            }
        }
        if(!findShape) selectShape = null
        selectShape?.toFront()
        startX = x
        startY = y
        state = STATE.DRAG
        notifyObservers()
    }

    fun drag(x: Double, y: Double) {
        if(state == STATE.DRAG) {
            val cur = selectShape
            when (cur) {
                is Line -> {
                    val dx = x - startX
                    val dy = y - startY
                    cur.startX += dx
                    cur.startY += dy
                    cur.endX += dx
                    cur.endY += dy
                    startX = x
                    startY = y
                }
                is Rectangle -> {
                    val dx = x - startX
                    val dy = y - startY
                    cur.x += dx
                    cur.y += dy
                    startX = x
                    startY = y
                }
                is Circle -> {
                    val dx = x - startX
                    val dy = y - startY
                    cur.centerX += dx
                    cur.centerY += dy
                    startX = x
                    startY = y
                }
            }
        }

        notifyObservers()
    }

    fun erase(x: Double, y:Double) {
        selectShape = null
        var eraseShape:Shape? = null
        for(shape in root.children) {
            if(shape is Line) {
                val q = closestPoint(Point2D(x, y), Point2D(shape.startX, shape.startY), Point2D(shape.endX, shape.endY))
                if(Point2D(x, y).distance(q) <= shape.strokeWidth / 2) {
                    eraseShape = shape
                }
            }
            else if(shape is Rectangle) {
                if(x >= shape.x && x <= shape.x + shape.width && y >= shape.y && y <= shape.y + shape.height) {
                    eraseShape = shape
                }
            }
            else if(shape is Circle) {
                if(Point2D(x, y).distance(Point2D(shape.centerX, shape.centerY)) <= shape.radius) {
                    eraseShape = shape
                }
            }
        }
        root.children.remove(eraseShape)
        notifyObservers()
    }

    fun escapeKey() {
        selectShape = null
        notifyObservers()
    }

    fun remove() {
        if(selectShape != null) {
            root.children.remove(selectShape)
        }
        selectShape = null
        notifyObservers()
    }

    fun paint(x: Double, y: Double) {
        selectShape = null
        var paintShape:Shape? = null
        for(shape in root.children) {
            if(shape is Rectangle) {
                if(x >= shape.x && x <= shape.x + shape.width && y >= shape.y && y <= shape.y + shape.height) {
                    paintShape = shape
                }
            }
            else if(shape is Circle) {
                if(Point2D(x, y).distance(Point2D(shape.centerX, shape.centerY)) <= shape.radius) {
                    paintShape = shape
                }
            }
        }
        paintShape?.fill = fillColor
        notifyObservers()
    }

    fun changeline(value: Color) {
        lineColor = value
        val cur = selectShape
        if(cur != null) {
            when (cur) {
                is Line -> {
                    cur.stroke = value
                }
                is Rectangle -> {
                    cur.stroke = value
                }
                is Circle -> {
                    cur.stroke = value
                }
            }
        }
        notifyObservers()
    }

    fun changefill(value: Color) {
        fillColor = value
        val cur = selectShape
        when (cur) {
            is Rectangle -> {
                cur.fill = value
            }
            is Circle -> {
                cur.fill = value
            }
        }
        notifyObservers()
    }

    fun changewidth(value: Double) {
        strokeWidth = value
        val cur = selectShape
        when (cur) {
            is Line -> {
                cur.strokeWidth = value
            }
            is Rectangle -> {
                cur.strokeWidth = value
            }
            is Circle -> {
                cur.strokeWidth = value
            }
        }
        notifyObservers()
    }

    fun changestyle(value: String) {
        linestyle = value
        val cur = selectShape
        when (cur) {
            is Line -> {
                cur.style = value
            }
            is Rectangle -> {
                cur.style = value
            }
            is Circle -> {
                cur.style = value
            }
        }
        notifyObservers()
    }

    fun cut() {
        if(selectShape != null) {
            iscut = true
            copyShape = selectShape
            root.children.remove(selectShape)
            selectShape = null
        }
        notifyObservers()
    }

    fun copy() {
        if(selectShape != null) {
            copyShape = selectShape
        }
        notifyObservers()
    }

    fun paste() {
        val cur = copyShape
        if(cur != null) {
            if(cur is Line) {
                line = Line()
                line.style = cur.style
                line.stroke = cur.stroke
                line.strokeWidth = cur.strokeWidth
                line.startX = cur.startX + 10.0
                line.startY = cur.startY + 10.0
                line.endX = cur.endX + 10.0
                line.endY = cur.endY + 10.0
                root.children.add(line)
            }
            else if(cur is Rectangle) {
                rect = Rectangle()
                rect.fill = cur.fill
                rect.style = cur.style
                rect.stroke = cur.stroke
                rect.strokeWidth = cur.strokeWidth
                rect.x = cur.x + 10.0
                rect.y = cur.y + 10.0
                rect.width = cur.width
                rect.height = cur.height
                root.children.add(rect)
            }
            else if(cur is Circle) {
                circle = Circle()
                circle.fill = cur.fill
                circle.style = cur.style
                circle.stroke = cur.stroke
                circle.strokeWidth = cur.strokeWidth
                circle.centerX = cur.centerX + 10.0
                circle.centerY = cur.centerY + 10.0
                root.children.add(circle)
            }
            canvas.toFront()
        }
        if(iscut) {
            copyShape = null
            iscut = false
        }
        notifyObservers()
    }
}