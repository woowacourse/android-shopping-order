package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.shoppingCart.DefaultShippingRule
import woowacourse.shopping.domain.shoppingCart.ShippingRule
import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct
import java.time.LocalDate

class FreeShipping(
    override val description: String,
    override val code: String,
    override val explanationDate: LocalDate,
    override val id: Long,
    override val minimumAmount: Int?,
) : Coupon(), ShippingRule by DefaultShippingRule() {
    override fun discountAmount(shoppingCartProductToOrder: List<ShoppingCartProduct>): Int {
        return shippingFee
    }
}
