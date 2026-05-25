package woowacourse.shopping.domain

import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.domain.model.order.Order

fun applyCouponUseCase(
    orderRepository: OrderRepository,
    order: Order,
    couponCode: String?,
): Order {
    val coupon =
        orderRepository.coupons.value.find { it.code == couponCode }
            ?: return order
    return coupon.apply(order)
}
