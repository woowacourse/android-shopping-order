package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct
import java.time.LocalDate

data class Fixed(
    override val description: String,
    override val code: String,
    override val expirationDate: LocalDate,
    override val id: Long,
    val discount: Int,
    override val minimumAmount: Int?,
) : Coupon() {
    override fun discountAmount(shoppingCartProductToOrder: List<ShoppingCartProduct>): Int {
        return discount
    }
}
