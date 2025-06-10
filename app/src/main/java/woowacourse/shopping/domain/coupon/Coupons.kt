package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct
import java.time.LocalDate
import java.time.LocalTime

data class Coupons(
    private val coupons: List<Coupon>,
    private val today: LocalDate = LocalDate.now(),
    private val now: LocalTime = LocalTime.now(),
) {
    operator fun get(id: Long): Coupon? {
        return coupons.find { it.id == id }
    }

    fun available(shoppingCartProductToOrder: List<ShoppingCartProduct>): List<Coupon> {
        return coupons.filter {
            it.isAvailable(shoppingCartProductToOrder, today, now)
        }
    }
}
