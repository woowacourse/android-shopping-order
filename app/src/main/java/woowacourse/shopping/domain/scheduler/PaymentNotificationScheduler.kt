package woowacourse.shopping.domain.scheduler

interface PaymentNotificationScheduler {
    fun schedule(
        orderItemsJson: String,
        orderAmount: Long,
    )

    fun cancel()
}
