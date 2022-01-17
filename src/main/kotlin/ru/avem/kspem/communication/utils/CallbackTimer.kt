package ru.avem.stand.utils

import javafx.util.Duration
import tornadofx.*
import java.util.*
import kotlin.concurrent.thread

class CallbackTimer(
    private val delay: Duration = 0.seconds,
    private val tickPeriod: Duration,
    private val tickTimes: Int = 1,
    private val onStartJob: (tcb: ICallbackTimer) -> Unit = {},
    private val tickJob: (tcb: ICallbackTimer) -> Unit = {},
    private val onFinishJob: (tcb: ICallbackTimer) -> Unit = {},
    private var timerName: String = "default_timer"
) : ICallbackTimer {
    val timer = Timer(timerName)
    var currentTick = 0

    private val timerTask = object : TimerTask() {
        override fun run() {
            currentTick++
            tickJob(this@CallbackTimer)

            if (currentTick == tickTimes) {
                timer.cancel()
                onFinishJob(this@CallbackTimer)
            }
        }
    }

    override fun getName() = timerName

    override fun start() {
        thread {
            onStartJob(this)
            timer.schedule(timerTask, delay.toMillis().toLong(), tickPeriod.toMillis().toLong())
        }
    }

    override fun stop() {
        timer.cancel()
    }

    override fun getCurrentTicks() = currentTick
}

interface ICallbackTimer {
    fun getName(): String
    fun start()
    fun stop()
    fun getCurrentTicks(): Int
}
