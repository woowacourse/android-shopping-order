package woowacourse.shopping.domain.coupon

import java.time.LocalDateTime
import woowacourse.shopping.domain.CartContent
import woowacourse.shopping.feature.purchase.PurchaseConfig

data class OrderContext(
    val totalPrice: Int = 0,
    val items: List<CartContent> = emptyList(),
    val now: LocalDateTime = LocalDateTime.now(),
    val shippingFee: Int = PurchaseConfig.SHIPPING_PRICE,
)
