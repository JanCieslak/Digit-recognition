import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import javax.imageio.ImageIO

data class Entry(
        val label: Int,
        val data: FloatArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Entry

        if (label != other.label) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = label
        result = 31 * result + data.contentHashCode()
        return result
    }
}

data class Distance(
        val label: Int,
        val distance: Float
)

class Knn(
        private val k: Int,
        private val distanceFunction: IDistanceFunction,
) {
    private val entries = ArrayList<Entry>()

    @Volatile
    var dataLoaded = false

    fun loadData() {
        for (label in 0..9) {
            val size = Files.list(Path.of("resources/images/$label")).count()
            for (n in 0 until size) {
                val image = ImageIO.read(File("resources/images/$label/img_$n.png"))
                val array = FloatArray(28 * 28)
                fillArray(array, image)
                entries.add(Entry(label, array))
            }
        }

        println("Data loaded")
        dataLoaded = true
    }

    fun classify(testArray: FloatArray): Int {
        val distances = ArrayList<Distance>()

        for (entry in entries) {
            val distance = distanceFunction.distance(testArray, entry.data)
            distances.add(Distance(entry.label, distance))
        }

        distances.sortBy { distance -> distance.distance }
        val knn = distances.subList(0, k)
        val guessMap = HashMap<Int, Int>()

        for (label in 0..9) {
            guessMap[label] = 0
        }

        for (neighbour in knn) {
            guessMap.compute(neighbour.label) { k, v -> v!! + 1 }
        }

        var topGuess = -1
        var topLabel = -1

        for (entry in guessMap.entries) {
            if (entry.value > topGuess) {
                topLabel = entry.key
                topGuess = entry.value
            }
        }

        return topLabel
    }
}