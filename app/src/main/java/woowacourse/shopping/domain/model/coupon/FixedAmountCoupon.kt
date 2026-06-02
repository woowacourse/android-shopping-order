package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.order.Order
import java.time.LocalDate

data class FixedAmountCoupon(
    override val code: String,
    override val name: String,
    override val expirationDate: LocalDate,
    val discountAmount: Int,
    override val minimumOrderAmount: Int,
) : Coupon(
    code = code,
    name = name,
    expirationDate = expirationDate,
    minimumOrderAmount = minimumOrderAmount,
) {
    init {
        require(discountAmount > 0) { "할인 금액은 0원 초과여야 합니다." }
        require(minimumOrderAmount >= 0) { "최소 주문 금액은 0원 이상이어야 합니다." }
    }

    override fun discountAmount(
        order: Order,
        context: CouponContext,
    ): Int = if (order.orderAmount >= minimumOrderAmount) discountAmount else 0
}