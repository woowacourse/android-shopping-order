package woowacourse.shopping.domain

import woowacourse.shopping.domain.model.order.Coupon
import woowacourse.shopping.domain.model.order.Order
import woowacourse.shopping.domain.repository.OrderRepository

fun getAvailableCouponUseCase(
    orderRepository: OrderRepository,
    order: Order,
): List<Coupon> = orderRepository.coupons.value.filter { it.isApplicable(order) }
