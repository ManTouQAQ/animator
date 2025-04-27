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
                hoverAnimation.update(0, true)
            }
        }

    val hoverAnimation = Animation(
        1000L,
        { p ->
            size = defaultSize + (p / 4)
            if (!rainbow){
                color = AnimationUtils.lerpColor(
                    defaultColor,
                    hoverColor,
                    p
                )
            }
            angle = AnimationUtils.lerp(defaultAngle, 180f, p)
            println("Progress: $p")
        },
        onEnd = {
            println("End")
        }
    )

    fun update(deltaMillis: Long) {
        hoverAnimation.update(deltaMillis * if (isHovering) -1 else 1)
        if (rainbow) {
            color = AnimationUtils.hsvColorCyclic(0.05f)
        }
    }
}