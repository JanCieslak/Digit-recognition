import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.image.BufferedImage
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel
import kotlin.concurrent.thread

fun main() {
    val frame = JFrame("Digit recognition")
    val panel = JPanel()
    val drawingPanel = DrawingPanel(25)
    val button = JButton("Guess the number")
    val knn = Knn(10, EuclideanDistanceFunction())

    // todo replace with coroutines
    thread {
        knn.loadData()
    }

    panel.minimumSize = Dimension(280, 320)
    panel.maximumSize = Dimension(280, 320)
    panel.preferredSize = Dimension(280, 320)

    drawingPanel.background = Color.WHITE
    drawingPanel.minimumSize = Dimension(280, 280)
    drawingPanel.maximumSize = Dimension(280, 280)
    drawingPanel.preferredSize = Dimension(280, 280)

    button.minimumSize = Dimension(280, 40)
    button.maximumSize = Dimension(280, 40)
    button.preferredSize = Dimension(280, 40)

    panel.layout = BorderLayout()
    panel.add(drawingPanel, BorderLayout.NORTH)
    panel.add(button, BorderLayout.SOUTH)

    frame.add(panel)
    frame.pack()

    frame.setLocationRelativeTo(null)
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.isVisible = true

    button.addActionListener {
        val savedImage = drawingPanel.save()

        // container for test image
        val testImage = BufferedImage(28, 28, BufferedImage.TYPE_INT_RGB)

        // scaled version of drawn image
        val image2 = savedImage.getScaledInstance(28, 28, BufferedImage.SCALE_SMOOTH)

        // copy scaled version to buffered image
        val graphics = testImage.createGraphics()
        graphics.drawImage(image2, 0, 0, 28, 28, null)
        graphics.dispose()

        val testArray = FloatArray(testImage.width * testImage.height)
        fillArray(testArray, testImage)

        while (!knn.dataLoaded) { }

        val label = knn.classify(testArray)
        println("Is that $label ???")
    }
}

/**
 * Fills the [array] with luminance values from the [image]
 */
fun fillArray(array: FloatArray, image: BufferedImage) {
    for (w in 0 until image.width) {
        for (h in 0 until image.height) {
            val color = image.getRGB(h, w)
            val red = (color ushr 16) and 0xFF
            val green = (color ushr 8) and 0xFF
            val blue = (color and 0xFF)
            val luminance = (red * 0.2126f + green * 0.7152f + blue * 0.0722f) / 255.0f
            val index = (w * image.width + h)
            array[index] = luminance
        }
    }
}