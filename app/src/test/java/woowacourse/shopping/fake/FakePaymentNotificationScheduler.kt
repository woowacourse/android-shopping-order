package woowacourse.shopping.fake

import woowacourse.shopping.domain.scheduler.PaymentNotificationScheduler

class FakePaymentNotificationScheduler : PaymentNotificationScheduler {
    var scheduleCallCount: Int = 0
        private set

    var cancelCallCount: Int = 0
        private set

    var lastScheduledOrderItemsJson: String? = null
        private set

    var lastScheduledOrderAmount: Long? = null
        private set

    override fun schedule(
        orderItemsJson: String,
        orderAmount: Long,
    ) {
        scheduleCallCount++
        lastScheduledOrderItemsJson = orderItemsJson
        lastScheduledOrderAmount = orderAmount
    }

    override fun cancel() {
        cancelCallCount++
    }
}
