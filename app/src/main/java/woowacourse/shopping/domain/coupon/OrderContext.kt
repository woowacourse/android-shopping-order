package woowacourse.shopping.domain.coupon

import java.time.LocalTime
import woowacourse.shopping.domain.CartContent
import woowacourse.shopping.feature.purchase.PurchaseConfig

data class OrderContext(
    val totalPrice: Int = 0,
    val items: List<CartContent> = emptyList(),
    val now: LocalTime = LocalTime.now(),
    val shippingFee: Int = PurchaseConfig.SHIPPING_PRICE,
)
