package woowacourse.shopping.domain.notification

interface PaymentNotificationScheduler {
    fun schedule(selectedCartItemIds: List<Long>)

    fun cancel(selectedCartItemIds: List<Long>)
}
