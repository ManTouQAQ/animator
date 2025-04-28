package me.mantou.animator

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AnimationTest {
    private val progressValues = mutableListOf<Float>()

    @BeforeTest
    fun resetProgressValues() {
        progressValues.clear()
    }

    @Test
    fun testInterpolator() {
        val animation = Animation(
            1000L,
            { progress -> progressValues.add(progress) },
            { t -> t * t }
        )

        animation.update(500L) // 0.5^2
        assertEquals(0.25f, progressValues.last())

        animation.update(250L) // (0.4 + 0.25)^2
        assertEquals(0.5625f, progressValues.last())
    }

    @Test
    fun testRepeatRestart() {
        var endFlag = false
        val animation = Animation(
            1000L,
            { progress -> progressValues.add(progress) },
            onEnd = { endFlag = true },
            repeatMode = Animation.RepeatMode.RESTART,
            repeatCount = 2
        )

        animation.update(600L) // 60%
        assertEquals(0.6f, progressValues.last())

        animation.update(600L) // 60% + 60%
        assertEquals(0.2f, progressValues.last())

        animation.update(900L) // 20% + 90%
        assertEquals(0.1f, progressValues.last())

        animation.update(900L) // 10% + 90%
        assertEquals(1.0f, progressValues.last())
        assertTrue(endFlag)
    }

    @Test
    fun testRepeatReverse() {
        var endFlag = false
        val animation = Animation(
            1000L,
            { progress -> progressValues.add(progress) },
            onEnd = { endFlag = true },
            repeatMode = Animation.RepeatMode.REVERSE,
            repeatCount = 2
        )

        // 前进
        animation.update(600L) // 60%
        assertEquals(0.6f, progressValues.last())

        animation.update(600L) // 120% - 100%
        assertEquals(0.8f, progressValues.last())

        animation.update(600L) // 80% - 60%
        assertEquals(0.2f, progressValues.last(), 0.01f)

        animation.update(300L) // 20% - 30%
        assertEquals(0.1f, progressValues.last())

        animation.update(900L) // 10% + 90%
        assertEquals(1.0f, progressValues.last())
        assertTrue(endFlag)
    }

    @Test
    fun testReverse(){
        val animation = Animation(
            1000L,
            { progress -> progressValues.add(progress) },
            repeatMode = Animation.RepeatMode.REVERSE,
            repeatCount = Animation.INFINITE
        )

        animation.update(600L)
        assertEquals(0.6f, progressValues.last())
        animation.reverse()
        animation.update(200L)
        assertEquals(0.2f, progressValues.last(), 0.01f)
    }

    @Test
    fun testPauseAndResume() {
        val animation = Animation(
            1000L,
            { progress -> progressValues.add(progress) }
        )

        animation.update(500L)

        animation.pause()
        assertFalse(animation.isRunning)

        animation.update(200L)
        assertEquals(0.5f, progressValues.last())

        animation.resume()
        assertTrue(animation.isRunning)

        animation.update(300L)
        assertEquals(0.8f, progressValues.last())
    }

    @Test
    fun testRollback() {
        val animation = Animation(
            1000L,
            { progress -> progressValues.add(progress) },
        )

        animation.update(500L)

        animation.update(-200L)
        assertEquals(0.3f, progressValues.last())

        animation.update(200L)
        assertEquals(0.5f, progressValues.last())
    }

    @Test
    fun testReset() {
        val animation = Animation(
            1000L,
            { progress -> progressValues.add(progress) }
        )

        animation.update(500L)

        // 重置动画
        animation.reset()
        assertTrue(animation.elapsedTime == 0L)

        animation.update(300L)
        assertEquals(0.3f, progressValues.last())
    }

    @Test
    fun testEnd() {
        var endFlag = 0
        var animation = Animation(
            1000L,
            { _ -> },
            onEnd = { endFlag++ },
        )

        animation.update(1000L)
        assertTrue(animation.isEnd())

        animation = Animation(
            1000L,
            { _ -> },
            repeatCount = 2
        )

        animation.update(1000L)
        assertFalse(animation.isEnd())

        animation.update(2000L)
        assertTrue(animation.isEnd())
    }
}