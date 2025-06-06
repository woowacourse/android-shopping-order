package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct
import java.time.LocalDate
import java.time.LocalTime

data class Coupons(
    val coupons: List<Coupon>,
    val today: LocalDate = LocalDate.now(),
    val now: LocalTime = LocalTime.now(),
) {
    fun available(shoppingCartProductToOrder: List<ShoppingCartProduct>): List<Coupon> {
        return coupons.filter {
            it.isAvailable(shoppingCartProductToOrder, today, now)
        }
    }
}
