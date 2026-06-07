package woowacourse.shopping.viewmodel.fakes

import woowacourse.shopping.ui.alarm.AlarmScheduler

class FakeAlarmScheduler : AlarmScheduler {
    var createAlarmCalled = false
    var cancelCalledCount = 0
    var lastDelayTime: Long? = null

    override fun createAlarmSchedule(delayTime: Long) {
        createAlarmCalled = true
        lastDelayTime = delayTime
    }

    override fun cancel() {
        cancelCalledCount++
    }
}
