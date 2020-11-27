import java.awt.Graphics
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JPanel
import javax.swing.event.MouseInputListener

data class Point(
        val x: Int,
        val y: Int,
)

class DrawingPanel(private val paintSize: Int) : JPanel(), MouseInputListener {
    private val halfPaintSize = paintSize / 2
    private var mousePressed = false
    private var mouseX = 0
    private var mouseY = 0
    private val points = ArrayList<Point>()

    init {
        addMouseListener(this)
        addMouseMotionListener(this)
    }

    fun save(): BufferedImage {
        val bufImage = BufferedImage(this.preferredSize.width, this.preferredSize.height, BufferedImage.TYPE_INT_RGB)
        val bufImageGfx = bufImage.createGraphics()

        this.print(bufImageGfx)
        bufImageGfx.dispose()

        ImageIO.write(bufImage, "png", File("resources/test_image.png"))

        return bufImage
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)

        for (point in points) {
            g!!.fillOval(point.x - halfPaintSize, point.y - halfPaintSize, paintSize, paintSize)
        }
    }

    override fun mouseClicked(e: MouseEvent?) { }

    override fun mousePressed(e: MouseEvent?) {
        mousePressed = true
        mouseX = e!!.x
        mouseY = e.y
        points.clear()
    }

    override fun mouseReleased(e: MouseEvent?) {
        mousePressed = false
    }

    override fun mouseEntered(e: MouseEvent?) { }

    override fun mouseExited(e: MouseEvent?) { }

    override fun mouseDragged(e: MouseEvent?) {
        mouseX = e!!.x
        mouseY = e.y
        points.add(Point(mouseX, mouseY))
        this.repaint()
    }

    override fun mouseMoved(e: MouseEvent?) { }
}