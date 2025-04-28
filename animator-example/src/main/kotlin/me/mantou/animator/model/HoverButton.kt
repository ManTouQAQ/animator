package me.mantou.animator.model

import me.mantou.animator.Animation
import me.mantou.animator.util.AnimationUtils
import me.mantou.animator.util.multiply
import me.mantou.animator.util.toIntValueString
import java.awt.Color

data class ButtonProps(
    var size: Float = 0.5f,
    var angle: Float = 0f,
    var color: Color = Color(1f, 1f, 1f, 1f)
) {
    override fun toString(): String {
        return "size=$size, angle=$angle, color=[${color.toIntValueString()}]"
    }
}

class HoverButton {
    val currentProps = ButtonProps()
    val baseProps = ButtonProps()
    val hoverProps = ButtonProps(0.6f, 180f, Color(0f, 1f, 1f, 1f))

    var isHovering = false
    var rainbow = false
        set(value) {
            field = value
            if (!value) {
                hoverAnimation.updateForce()
            }
        }

    val hoverAnimation = Animation(
        1000L,
        { p ->
            currentProps.size = AnimationUtils.lerp(
                baseProps.size,
                hoverProps.size,
                p
            )
            currentProps.color = AnimationUtils.lerpColor(
                baseProps.color,
                hoverProps.color,
                p
            )
            currentProps.angle = AnimationUtils.lerp(
                baseProps.angle,
                hoverProps.angle,
                p
            )
            println("Progress: $p")
        },
        onEnd = {
            println("End")
        }
    )

    val effectAnimations = mutableListOf<Animation>()
    val overlayColors = mutableMapOf<Animation, Color>()

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

    fun getFinalColor(): Color {
        var finalColor = currentProps.color
        for (overlayColor in overlayColors.values) {
            finalColor = finalColor.multiply(overlayColor)
        }
        return finalColor
    }

    fun click(){
        effectAnimations.add(Animation(
            200L,
            { p ->
                overlayColors[this] = AnimationUtils.lerpColor(
                    Color(1f, 1f, 1f, 1f),
                    Color(0.7f, 0.7f, 0.7f, 1f),
                    p
                )
            },
            onEnd = {
                overlayColors.remove(this)
            },
            repeatMode = Animation.RepeatMode.REVERSE,
            repeatCount = 1
        ))
    }
}