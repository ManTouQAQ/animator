package me.mantou.animator.model

import me.mantou.animator.Animation
import me.mantou.animator.util.AnimationUtils
import java.awt.Color

class HoverButton {
    var defaultAngle = 0f
    var defaultSize = 0.5f
    var defaultColor = Color(1f, 1f, 1f, 1f)
    var hoverColor = Color(0f, 1f, 1f, 1f)

    var size = defaultSize
    var angle = defaultAngle
    var color: Color = defaultColor

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
            size = defaultSize + (p / 4)
            color = AnimationUtils.lerpColor(
                defaultColor,
                hoverColor,
                p
            )
            angle = AnimationUtils.lerp(defaultAngle, 180f, p)
            println("Progress: $p")
        },
        onEnd = {
            println("End")
        }
    )

    val effectAnimations = mutableListOf<Animation>()

    fun update(deltaMillis: Long) {
        hoverAnimation.update(deltaMillis * if (isHovering) -1 else 1)

        effectAnimations.removeIf {
            it.update(deltaMillis)
            it.isEnd()
        }

        if (rainbow) {
            color = AnimationUtils.hsvColorCyclic(0.05f)
        }
    }

    fun click(){
        val darkenColor = Color(
            (color.red / 255f * 0.7f).coerceIn(0f, 1f),
            (color.green / 255f * 0.7f).coerceIn(0f, 1f),
            (color.blue / 255f * 0.7f).coerceIn(0f, 1f),
            color.alpha / 255f
        )

        effectAnimations.add(Animation(
            150L,
            { p ->
                hoverAnimation.updateForce()
                color = AnimationUtils.lerpColor(color, darkenColor, p)
            },
            repeatMode = Animation.RepeatMode.REVERSE,
            repeatCount = 1
        ))
    }
}