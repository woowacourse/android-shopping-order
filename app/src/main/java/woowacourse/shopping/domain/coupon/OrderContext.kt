package woowacourse.shopping.domain.coupon

import java.time.LocalTime
import woowacourse.shopping.domain.CartContent

data class OrderContext(
    val totalPrice: Int = 0,
    val items: List<CartContent> = emptyList(),
    val now: LocalTime = LocalTime.now(),
    val shippingFee: Int = 3000,
)
