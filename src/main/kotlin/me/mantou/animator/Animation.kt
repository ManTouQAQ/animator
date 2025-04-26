package me.mantou.animator

class Animation(
    private val durationMillis: Long,
    private val onUpdate: (progress: Float) -> Unit,
    private val interpolator: Interpolator = Interpolator { t -> t },
    private val onEnd: (() -> Unit)? = null,
    private val repeatMode: RepeatMode = RepeatMode.RESTART,
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
    var isEnd = false
        private set

    fun update(deltaMillis: Long, rollback: Boolean = false) {
        checkEnd()

        if (!isRunning) return

        elapsedMillis += deltaMillis * (if (rollback) -1 else 1)

        updateProgress()
    }

    private fun endAnimation() {
        isEnd = true
        isRunning = false
        val finalProgress = if (isReversed) 0f else 1f
        onUpdate(interpolator.interpolate(finalProgress))
        onEnd?.invoke()
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
                }else{
                    cycleTime.toFloat() / durationMillis.toFloat()
                }
            }
        }

        if (repeatCount != INFINITE && elapsedMillis >= durationMillis * (repeatCount + 1)) {
            endAnimation()
            return
        }

        progress = progress.coerceIn(0f, 1f)
        progress = interpolator.interpolate(progress)
        onUpdate(progress)
    }

    fun reverse() {
        checkEnd()
        if (repeatMode != RepeatMode.REVERSE) throw IllegalArgumentException("RepeatMode must be REVERSE")

        isReversed = !isReversed
    }

    fun reset() {
        checkEnd()

        elapsedMillis = 0L
        isReversed = false
    }

    fun pause(){
        checkEnd()
        isRunning = false
    }

    fun resume(){
        checkEnd()
        isRunning = true
    }

    fun setTime(time: Long) {
        checkEnd()
        elapsedMillis = time
        updateProgress()
    }

    private fun checkEnd(){
        if (isEnd) throw IllegalStateException("This animation is ended")
    }
}