package woowacourse.shopping.domain.model.order

import woowacourse.shopping.domain.model.PaymentItems
import java.time.LocalDateTime
import kotlin.math.max

data class Order(
    val dateTime: LocalDateTime,
    val items: PaymentItems,
    val deliveryFee: DeliveryFee,
    val discountAmount: Long,
    val deliveryLocation: DeliveryLocation,
    val appliedCouponCode: String? = null,
) {
    val finalAmount: Long
        get() = max(0, items.totalPrice + deliveryFee.price - discountAmount)
}
