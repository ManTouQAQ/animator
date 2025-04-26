package me.mantou.animator.util

import me.mantou.animator.Interpolator
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

object Interpolators{
    val Linear = Interpolator { t -> t }

    val Accelerate = Interpolator { t -> t * t }

    val Decelerate = Interpolator { t -> 1f - (1f - t) * (1f - t) }

    val AccelerateDecelerate = Interpolator { t ->
        (cos((t + 1) * PI) / 2.0 + 0.5).toFloat()
    }

    val Reverse = Interpolator { t -> 1f - t }

    fun cycle(cycles: Int): Interpolator {
        return Interpolator { t -> sin((2 * PI).toFloat() * t * cycles) }
    }

    fun steps(count: Int): Interpolator {
        require(count > 1) { "Step count must be greater than 1" }
        return Interpolator { t ->
            ((t * count).toInt().toFloat() / (count - 1)).coerceIn(0f, 1f)
        }
    }

    fun elastic(amplitude: Float = 1.0f, period: Float = 0.3f): Interpolator {
        return Interpolator { t ->
            if (t == 0f || t == 1f) t else {
                val s = period / 4f
                (amplitude * (2f).pow(-10f * t) * sin((t - s) * (2 * PI).toFloat() / period) + 1f)
            }
        }
    }

    fun overshoot(tension: Float): Interpolator {
        return Interpolator { t ->
            val t1 = t - 1f
            t1 * t1 * ((tension + 1) * t1 + tension) + 1f
        }
    }
}