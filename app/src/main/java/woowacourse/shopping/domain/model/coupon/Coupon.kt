package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.order.Order
import java.time.LocalDate
import java.time.LocalTime

sealed class Coupon(
    open val code: String,
    open val name: String,
    open val expirationDate: LocalDate,
    open val minimumOrderAmount: Int? = null,
) {
    abstract fun discountAmount(
        order: Order,
        context: CouponContext = CouponContext(),
    ): Int

    open fun deliveryFee(
        order: Order,
        defaultDeliveryFee: Int,
        context: CouponContext = CouponContext(),
    ): Int = defaultDeliveryFee
}

data class CouponContext(
    val currentTime: LocalTime = LocalTime.now(),
)
