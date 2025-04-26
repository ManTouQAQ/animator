package me.mantou.animator

fun interface Interpolator {
    fun interpolate(t: Float): Float
}