package me.mantou.animator

class Animation(
    val durationMillis: Long,
    private val onUpdate: (progress: Float) -> Unit,
    var interpolator: Interpolator = Interpolator { t -> t },
    private val onEnd: (() -> Unit)? = null,
    var repeatMode: RepeatMode = RepeatMode.RESTART,
    private val repeatCount: Int = 0
) {
    enum class RepeatMode {
        RESTART, REVERSE,
    }

    companion object {
        const val INFINITE = -1
    }

    var elapsedMillis = 0L
        private set
    var isReversed = false
        private set
    var isRunning = true
        private set

    private var endFlag = false
    private var initFlag = false

    fun update(deltaMillis: Long, ignoreBorder: Boolean = false) {
        if (!isRunning) return

        updateTime(deltaMillis, ignoreBorder)
    }

    private fun updateTime(deltaMillis: Long, ignoreBorder: Boolean = false) {
        var targetMillis = elapsedMillis + deltaMillis

        if (targetMillis <= 0L) {
            targetMillis = 0
            if (!initFlag){
                initFlag = true
            }else if (!ignoreBorder) return
        } else {
            initFlag = false
        }

        if (repeatCount != INFINITE && targetMillis >= durationMillis * (repeatCount + 1)) {
            if (!endFlag) {
                endFlag = true
                elapsedMillis = durationMillis * (repeatCount + 1)
                endAnimation()
                return
            }else if (ignoreBorder) {
                updateEndProgress()
            }
            return
        } else {
            endFlag = false
        }

        elapsedMillis = targetMillis

        updateProgress()
    }

    private fun endAnimation() {
        updateEndProgress()
        onEnd?.invoke()
    }

    private fun updateEndProgress() {
        val finalProgress = if (isReversed) 0f else 1f
        onUpdate(interpolator.interpolate(finalProgress))
    }

    private fun updateProgress() {
        var progress = when (repeatMode) {
            RepeatMode.RESTART -> {
                val normalizedTime = ((elapsedMillis % durationMillis) + durationMillis) % durationMillis
                normalizedTime / durationMillis.toFloat()
            }

            RepeatMode.REVERSE -> {
                val cycle = elapsedMillis / durationMillis
                val cycleTime = elapsedMillis % durationMillis
                val shouldReverse = (cycle % 2 == 1L) xor isReversed
                if (shouldReverse) {
                    1f - cycleTime.toFloat() / durationMillis.toFloat()
                } else {
                    cycleTime.toFloat() / durationMillis.toFloat()
                }
            }
        }

        progress = progress.coerceIn(0f, 1f)
        progress = interpolator.interpolate(progress)
        onUpdate(progress)
    }

    fun reverse() {
        if (repeatMode != RepeatMode.REVERSE) throw IllegalArgumentException("RepeatMode must be REVERSE")

        isReversed = !isReversed
    }

    fun reset() {
        elapsedMillis = 0L
        isReversed = false
    }

    fun pause() {
        isRunning = false
    }

    fun resume() {
        isRunning = true
    }

    fun setTime(time: Long, ignoreBorder: Boolean = false) {
        updateTime(time - elapsedMillis, ignoreBorder)
    }

    fun isEnd(): Boolean {
        return endFlag
    }
}