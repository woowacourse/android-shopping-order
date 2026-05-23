package woowacourse.shopping.feature.fake

import java.time.LocalDateTime
import woowacourse.shopping.feature.notification.PaymentNotificationScheduler

class FakePaymentNotificationScheduler : PaymentNotificationScheduler {
    var scheduledAt: LocalDateTime? = null
        private set
    var scheduledContentIds: List<String> = emptyList()
        private set
    var scheduledTotalPrice: Int = 0
        private set
    var scheduleCallCount: Int = 0
        private set
    var cancelCallCount: Int = 0
        private set

    override fun scheduleAt(
        at: LocalDateTime,
        contentIds: List<String>,
        totalPrice: Int,
    ) {
        scheduledAt = at
        scheduledContentIds = contentIds
        scheduledTotalPrice = totalPrice
        scheduleCallCount++
    }

    override fun cancel() {
        cancelCallCount++
    }
}
