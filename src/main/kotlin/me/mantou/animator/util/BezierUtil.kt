package me.mantou.animator.util

object BezierUtil {
    fun cubicBezierSolve(t: Float, x1: Float, y1: Float, x2: Float, y2: Float, iterations: Int = 5): Float {
        var u = t
        repeat(iterations) {
            val x = cubicBezier(u, 0f, x1, x2, 1f)
            val dx = cubicBezierDerivative(u, 0f, x1, x2, 1f)
            if (dx == 0f) return@repeat
            u -= (x - t) / dx
            u = u.coerceIn(0f, 1f)
        }
        return cubicBezier(u, 0f, y1, y2, 1f)
    }

    fun cubicBezier(u: Float, p0: Float, p1: Float, p2: Float, p3: Float): Float {
        val oneMinusU = 1 - u
        return (oneMinusU * oneMinusU * oneMinusU) * p0 +
                3 * (oneMinusU * oneMinusU) * u * p1 +
                3 * oneMinusU * (u * u) * p2 +
                (u * u * u) * p3
    }

    fun cubicBezierDerivative(u: Float, p0: Float, p1: Float, p2: Float, p3: Float): Float {
        val oneMinusU = 1 - u
        return 3 * (oneMinusU * oneMinusU) * (p1 - p0) +
                6 * oneMinusU * u * (p2 - p1) +
                3 * (u * u) * (p3 - p2)
    }
}