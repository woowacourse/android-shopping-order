package woowacourse.shopping.domain

import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.domain.model.PaymentItems
import woowacourse.shopping.domain.model.payment.DeliveryFee
import woowacourse.shopping.domain.model.payment.DeliveryLocation
import woowacourse.shopping.domain.model.payment.Order
import java.time.LocalDateTime

class ApplyCouponUseCase(
    private val orderRepository: OrderRepository,
) {
    operator fun invoke(
        paymentItems: PaymentItems,
        couponCode: String?,
    ): Order {
        val defaultOrder =
            Order(
                dateTime = LocalDateTime.now(),
                items = paymentItems,
                deliveryFee = DeliveryFee(3_000L),
                discountAmount = 0L,
                deliveryLocation = DeliveryLocation.STANDARD,
            )
        val coupon = orderRepository.coupons.value.find { it.code == couponCode }
        return if (coupon != null) {
            runCatching { coupon.apply(defaultOrder) }.getOrDefault(defaultOrder)
        } else {
            defaultOrder
        }
    }
}
