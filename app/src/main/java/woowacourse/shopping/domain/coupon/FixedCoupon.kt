package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.cart.Receipt
import java.time.LocalDate
import java.time.LocalDateTime

class FixedCoupon(
    override val description: String,
    override val expirationDate: LocalDate,
    private val disCountPrice: Int,
    val minimumOrderPrice: Int,
) : Coupon {
    override fun isAvailable(receipt: Receipt, current: LocalDateTime): Boolean {
        return receipt.totalPrice >= minimumOrderPrice && current.toLocalDate() <= expirationDate
    }

    override fun discountPrice(receipt: Receipt): Int {
        return disCountPrice.coerceAtMost(receipt.totalPrice)
    }
}