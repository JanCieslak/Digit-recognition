import java.lang.RuntimeException
import kotlin.math.pow
import kotlin.math.sqrt

interface IDistanceFunction {
    /**
     * Calculates distance between two points in any dimension
     */
    fun distance(position1: FloatArray, position2: FloatArray): Float
}

class EuclideanDistanceFunction : IDistanceFunction {
    override fun distance(position1: FloatArray, position2: FloatArray): Float {
        if (position1.size != position2.size) {
            throw RuntimeException("Arrays should be the same size")
        }

        var distance = 0.0f

       for (i in position1.indices) {
            distance += (position1[i] - position2[i]).pow(2)
        }

        return sqrt(distance)
    }
}

class ManhattanDistanceFunction : IDistanceFunction {
    override fun distance(position1: FloatArray, position2: FloatArray): Float {
       return 0.0f
    }
}