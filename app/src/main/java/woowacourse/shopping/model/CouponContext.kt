package woowacourse.shopping.model

import java.time.Clock

data class CouponContext(
    val items: List<CartItem>,
    val totalAmount: Money,
    val clock: Clock
)
