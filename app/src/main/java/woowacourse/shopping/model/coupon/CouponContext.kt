package woowacourse.shopping.model.coupon

import woowacourse.shopping.model.Money
import woowacourse.shopping.model.cart.CartItem
import java.time.Clock

data class CouponContext(
    val items: List<CartItem>,
    val subtotal: Money,
    val shippingFee: Money,
    val clock: Clock,
)
