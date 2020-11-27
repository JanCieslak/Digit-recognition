import java.awt.image.BufferedImage
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import kotlin.system.exitProcess
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime


// todo add training data

/**
 * This file transforms trains-images and train labels into separate images in the resources folder
 * It takes some time to transform all of the data so i added a timer (around 300 sec on my laptop)
 */
@ExperimentalTime
fun main() {
    // gather the data from train-images and train-labels
    val trainingData = DataInputStream(FileInputStream("resources/train-images.idx3-ubyte"))

    val magicNumber = trainingData.readInt()
    val imageCount = trainingData.readInt()
    val rowCount = trainingData.readInt()
    val colCount = trainingData.readInt()

    val labelData = DataInputStream(FileInputStream("resources/train-labels.idx1-ubyte"))

    val labelMagicNumber = labelData.readInt()
    val labelCount = labelData.readInt()

    if (imageCount != labelCount) {
        println("Something went wrong")
        exitProcess(-1)
    }

    val bufImage = BufferedImage(colCount, rowCount, BufferedImage.TYPE_INT_ARGB)
    val pixelCount = colCount * rowCount
    val pixels = IntArray(pixelCount)

    val map = HashMap<Byte, Int>()
    (0..9).forEach { map[it.toByte()] = 0 }

    // generate images
    val elapsed = measureTime {
        for (i in 0 until imageCount) {
            for (p in 0 until pixelCount) {
                val gray = 255 - trainingData.readByte()
                pixels[p] = (0xFF000000 or ((gray shl 16).toLong()) or ((gray shl 8).toLong()) or gray.toLong()).toInt()
            }

            bufImage.setRGB(0, 0, colCount, rowCount, pixels, 0, colCount)

            val label = labelData.readByte()
            val outImage = File("resources/images/$label/img_${map[label]}.png")

            map.compute(label) { _, k -> k!! + 1 }

            ImageIO.write(bufImage, "png", outImage)
        }
    }

    println("$magicNumber - $imageCount - $rowCount - $colCount")
    println("$labelMagicNumber - $labelCount")
    println("Time: ${elapsed.inSeconds}s")
}