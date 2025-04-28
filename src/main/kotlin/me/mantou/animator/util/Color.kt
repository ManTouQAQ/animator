package me.mantou.animator.util

@JvmInline
value class Color(
    private val packed: Int
) {
    val rInt: Int get() = (packed shr 16) and 0xFF
    val gInt: Int get() = (packed shr 8) and 0xFF
    val bInt: Int get() = packed and 0xFF
    val aInt: Int get() = (packed shr 24) and 0xFF

    val rFloat: Float get() = rInt / 255f
    val gFloat: Float get() = gInt / 255f
    val bFloat: Float get() = bInt / 255f
    val aFloat: Float get() = aInt / 255f

    companion object {
        fun from(rgb: Int, alpha: Int = 255): Color {
            val packed = (alpha shl 24) or (rgb and 0xFFFFFF)
            return Color(packed)
        }

        fun from(r: Int, g: Int, b: Int, a: Int = 255): Color {
            val packed = (a shl 24) or (r shl 16) or (g shl 8) or b
            return Color(packed)
        }

        fun from(r: Float, g: Float, b: Float, a: Float = 1.0f): Color {
            return from(
                (r.coerceIn(0f, 1f) * 255).toInt(),
                (g.coerceIn(0f, 1f) * 255).toInt(),
                (b.coerceIn(0f, 1f) * 255).toInt(),
                (a.coerceIn(0f, 1f) * 255).toInt()
            )
        }

        fun from(hex: String): Color {
            var cleanHex = hex.trim()
                .removePrefix("#")
                .removePrefix("0x")
                .removePrefix("0X")

            if (cleanHex.length == 6) {
                cleanHex = "FF$cleanHex"
            } else if (cleanHex.length != 8) {
                throw IllegalArgumentException("Invalid hex color format: $hex")
            }

            val packed = cleanHex.toLong(16).toInt()
            return Color(packed)
        }
    }
}

fun java.awt.Color.redFloat(): Float = red / 255f
fun java.awt.Color.greenFloat(): Float = green / 255f
fun java.awt.Color.blueFloat(): Float = blue / 255f
fun java.awt.Color.alphaFloat(): Float = alpha / 255f
fun java.awt.Color.multiply(other: java.awt.Color): java.awt.Color {
    return java.awt.Color(
        this.red / 255f * other.red / 255f,
        this.green / 255f * other.green / 255f,
        this.blue / 255f * other.blue / 255f,
        this.alpha / 255f * other.alpha / 255f
    )
}
fun java.awt.Color.toIntValueString(withAlpha: Boolean = false): String {
    return "$red, $green, $blue${if (withAlpha) ", $alpha" else ""}"
}
fun java.awt.Color.toFloatValueString(withAlpha: Boolean = false): String {
    return "${redFloat()}, ${greenFloat()}, ${blueFloat()}${if (withAlpha) ", ${alphaFloat()}" else ""}"
}