package woowacourse.shopping.domain.scheduler

interface PaymentNotificationScheduler {
    fun schedule()

    fun cancel()
}
