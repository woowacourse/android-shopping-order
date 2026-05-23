package woowacourse.shopping.domain.coupon

import java.time.LocalDateTime
import woowacourse.shopping.domain.CartContent

data class OrderContext(
    val totalPrice: Int = 0,
    val items: List<CartContent> = emptyList(),
    val now: LocalDateTime = LocalDateTime.now(),
)
