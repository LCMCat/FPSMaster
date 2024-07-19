package top.fpsmaster.utils.math

import java.security.SecureRandom

object RandomUtils {

    @JvmStatic
    private var random = SecureRandom();

    @JvmStatic
    fun nextDouble(min: Double, max: Double) : Double{
        return max + (min - max) * random.nextDouble()
    }

    @JvmStatic
    fun nextFloat(min: Float, max: Float) : Float{
        return nextDouble(min.toDouble(), max.toDouble()).toFloat()
    }

    @JvmStatic
    fun nextInt(min: Int, max: Int) : Int{
        return nextDouble(min.toDouble(), max.toDouble()).toInt()
    }

}