package me.mantou.animator

class Animation(
    val durationTime: Long,
    private val onUpdate: Animation.(progress: Float) -> Unit,
    var interpolator: Interpolator = Interpolator { t -> t },
    private val onEnd: (Animation.() -> Unit)? = null,
    var repeatMode: RepeatMode = RepeatMode.RESTART,
    private val repeatCount: Int = 0
) {
    enum class RepeatMode {
        RESTART, REVERSE,
    }

    companion object {
        const val INFINITE = -1
    }

    var elapsedTime = 0L
        private set
    var isReversed = false
        private set
    var isRunning = true
        private set

    private var endFlag = false
    private var initFlag = false

    fun update(deltaTime: Long, ignoreBorder: Boolean = false) {
        if (!isRunning) return

        updateTime(deltaTime, ignoreBorder)
    }

    fun updateForce(){
        updateTime(0, true)
    }

    private fun updateTime(deltaTime: Long, ignoreBorder: Boolean = false) {
        var targetTime = elapsedTime + deltaTime

        if (targetTime <= 0L) {
            targetTime = 0
            if (!initFlag){
                initFlag = true
            }else if (!ignoreBorder) return
        } else {
            initFlag = false
        }

        if (repeatCount != INFINITE && targetTime >= durationTime * (repeatCount + 1)) {
            if (!endFlag) {
                endFlag = true
                elapsedTime = durationTime * (repeatCount + 1)
                endAnimation()
                return
            }else if (ignoreBorder) {
                updateEndProgress()
            }
            return
        } else {
            endFlag = false
        }

        elapsedTime = targetTime

        updateProgress()
    }

    private fun endAnimation() {
        updateEndProgress()
        onEnd?.invoke(this)
    }

    private fun updateEndProgress() {
        val cycle = elapsedTime / durationTime
        val shouldReverse = (cycle % 2 == 0L) xor isReversed

        val finalProgress = if (shouldReverse) 0f else 1f
        onUpdate(interpolator.interpolate(finalProgress))
    }

    private fun updateProgress() {
        var progress = when (repeatMode) {
            RepeatMode.RESTART -> {
                val normalizedTime = ((elapsedTime % durationTime) + durationTime) % durationTime
                normalizedTime / durationTime.toFloat()
            }

            RepeatMode.REVERSE -> {
                val cycle = elapsedTime / durationTime
                val cycleTime = elapsedTime % durationTime
                val shouldReverse = (cycle % 2 == 1L) xor isReversed
                if (shouldReverse) {
                    1f - cycleTime.toFloat() / durationTime.toFloat()
                } else {
                    cycleTime.toFloat() / durationTime.toFloat()
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
        elapsedTime = 0L
        isReversed = false
    }

    fun pause() {
        isRunning = false
    }

    fun resume() {
        isRunning = true
    }

    fun setTime(time: Long, ignoreBorder: Boolean = false) {
        updateTime(time - elapsedTime, ignoreBorder)
    }

    fun isEnd(): Boolean {
        return endFlag
    }
}