package woowacourse.shopping.feature.notification

import java.time.LocalDateTime

interface PaymentNotificationScheduler {
    fun scheduleAt(
        at: LocalDateTime,
        contentIds: List<String>,
        totalPrice: Int,
    )

    fun cancel()
}
