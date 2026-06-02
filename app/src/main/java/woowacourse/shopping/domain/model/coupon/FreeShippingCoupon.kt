package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.order.Order
import java.time.LocalDate

data class FreeShippingCoupon(
    override val code: String,
    override val name: String,
    override val expirationDate: LocalDate,
    override val minimumOrderAmount: Int,
) : Coupon(
    code = code,
    name = name,
    expirationDate = expirationDate,
    minimumOrderAmount = minimumOrderAmount,
) {
    init {
        require(minimumOrderAmount >= 0) { "최소 주문 금액은 0원 이상이어야 합니다." }
    }

    override fun discountAmount(
        order: Order,
        context: CouponContext,
    ): Int = 0

    override fun deliveryFee(
        order: Order,
        defaultDeliveryFee: Int,
        context: CouponContext,
    ): Int = if (order.orderAmount >= minimumOrderAmount) 0 else defaultDeliveryFee
}
