package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.cart.Cart
import java.time.LocalDate
import java.time.LocalDateTime

class BoGoCoupon(
    override val couponId: Long,
    override val expirationDate: LocalDate,
    private val buyQuantity: Int,
    private val getQuantity: Int,
) : Coupon {

    override fun isAvailable(cart: Cart, current: LocalDateTime): Boolean =
        current.toLocalDate() <= expirationDate && cart.hasEnoughItems(buyQuantity + getQuantity)

    override fun discountPrice(cart: Cart): Int {
        return cart.findMostExpensiveItemPrice(buyQuantity + getQuantity) * getQuantity
    }
}

