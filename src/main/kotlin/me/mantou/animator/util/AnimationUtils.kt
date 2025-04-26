package me.mantou.animator.util

import java.awt.Color

object AnimationUtils {
    fun lerp(start: Float, end: Float, progress: Float): Float {
        return start + (end - start) * progress
    }

    fun lerpColor(
        start: Color,
        end: Color,
        progress: Float,
    ): Color {
        val progress = progress.coerceIn(0f, 1f)
        val r = lerp(start.red / 255f, end.red / 255f, progress)
        val g = lerp(start.green / 255f, end.green / 255f, progress)
        val b = lerp(start.blue / 255f, end.blue / 255f, progress)
        return Color(r, g, b, 1f)
    }

    fun hsvColor(progress: Float, rate: Float = 1f): Color {
        val hue = progress * rate % 1f
        return Color.getHSBColor(hue, 1f, 1f)
    }

    fun hsvColorCyclic(rate: Float = 1f): Color {
        val time = System.currentTimeMillis() / 1000.0
        val hue = (time * rate % 1.0).toFloat()
        return Color.getHSBColor(hue, 1.0f, 1.0f)
    }
}