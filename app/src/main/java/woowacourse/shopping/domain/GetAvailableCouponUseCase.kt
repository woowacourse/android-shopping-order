package woowacourse.shopping.domain

import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.domain.model.order.Coupon
import woowacourse.shopping.domain.model.order.Order

fun getAvailableCouponUseCase(
    orderRepository: OrderRepository,
    order: Order,
): List<Coupon> = orderRepository.coupons.value.filter { it.isApplicable(order) }
