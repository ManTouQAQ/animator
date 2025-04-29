package me.mantou.animator.model

import me.mantou.animator.Animation
import me.mantou.animator.util.AnimationUtils
import me.mantou.animator.util.Interpolators
import me.mantou.animator.util.multiply
import me.mantou.animator.util.toIntValueString
import java.awt.Color
import kotlin.collections.set

data class ButtonProps(
    var size: Float = 0.5f, var angle: Float = 0f, var color: Color = Color(1f, 1f, 1f, 1f)
) {
    override fun toString(): String {
        return "size=%.2f, angle=%.2f°, color=[${color.toIntValueString()}]".format(size, angle)
    }
}

data class StatesModify(
    var size: Float? = null, var angle: Float? = null, var color: Color? = null
)

class HoverButton {
    private val currentProps = ButtonProps()

    var isHovering = false
    var rainbow = false
        set(value) {
            field = value
            if (!value) {
                hoverAnimation.updateForce()
            }
        }

    val hoverAnimation = Animation(1000L, { p ->
        currentProps.color = AnimationUtils.lerpColor(
            Color(1f, 1f, 1f, 1f), Color(0f, 1f, 1f, 1f), p
        )

        val size = AnimationUtils.lerp(
            0f, 0.1f, p
        )
        val angle = AnimationUtils.lerp(
            0f, 180f, p
        )
        statesModifies[this] = StatesModify(size, angle)
        println("Progress: $p")
    }, onEnd = {
        println("End")
    })

    val effectAnimations = mutableListOf<Animation>()
    val statesModifies = mutableMapOf<Any, StatesModify>()

    fun update(deltaMillis: Long) {
        hoverAnimation.update(deltaMillis * if (isHovering) -1 else 1)

        if (rainbow) {
            currentProps.color = AnimationUtils.hsvColorCyclic(0.05f)
        }

        effectAnimations.removeIf {
            it.update(deltaMillis)
            it.isEnd()
        }
    }

    fun getModifiedProps(): ButtonProps {
        val copy = currentProps.copy()

        for (modify in statesModifies.values) {
            modify.size?.let { copy.size += it }
            modify.angle?.let { copy.angle += it }
            modify.color?.let { copy.color = copy.color.multiply(it) }
        }

        return copy
    }

    fun getRawProps(): ButtonProps {
        return currentProps.copy()
    }

    fun click() {
        effectAnimations.add(
            Animation(
                200L, { p ->
                statesModifies[this] = StatesModify(
                    angle = AnimationUtils.lerp(0f, 5f, Interpolators.SmoothStep.interpolate(p)),
                    color = AnimationUtils.lerpColor(
                        Color(1f, 1f, 1f, 1f), Color(0.7f, 0.7f, 0.7f, 1f), p
                    )
                )
            }, onEnd = {
                statesModifies.remove(this)
            }, repeatMode = Animation.RepeatMode.REVERSE, repeatCount = 1
            )
        )
    }
}