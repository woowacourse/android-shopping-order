package woowacourse.shopping.domain.repository

interface PaymentReminderScheduler {
    fun schedule(selectedProductIds: Set<Long>)

    fun cancel()
}
