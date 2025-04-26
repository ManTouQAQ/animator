package me.mantou.animator.interpolator

import me.mantou.animator.Animation
import me.mantou.animator.Interpolator
import me.mantou.animator.util.Interpolators
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class InterpolatorsTest {
    private val progressValues = mutableListOf<Float>()

    fun genTestAnimation(interpolator: Interpolator): Animation {
        return Animation(
            1000L,
            { progress -> progressValues.add(progress) },
            interpolator
        )
    }

    @BeforeTest
    fun resetProgressValues() {
        progressValues.clear()
    }

    @Test
    fun test() {
        val animation = genTestAnimation(Interpolators.overshoot(1.5f))

        repeat(5) {
            animation.update(200L)
        }

        assertTrue(progressValues[0] < 1.0f)
        assertTrue(progressValues[2] > 1.0f)
        assertTrue(progressValues[3] > 1.0f)
        assertTrue(progressValues[4] == 1.0f)
    }
}