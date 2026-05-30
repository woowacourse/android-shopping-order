package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.PaymentItems
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.scheduler.PaymentNotificationScheduler

class PlaceOrderUseCase(
    private val orderRepository: OrderRepository,
    private val notificationScheduler: PaymentNotificationScheduler,
) {
    suspend operator fun invoke(items: PaymentItems) {
        val cartItemIds = items.getPaymentItems().map { it.id }
        orderRepository.requestOrder(cartItemIds)
        notificationScheduler.cancel()
    }
}
