package woowacourse.shopping.ui.alarm

interface AlarmScheduler {
    fun createAlarmSchedule(delayTime: Long)

    fun cancel()
}
