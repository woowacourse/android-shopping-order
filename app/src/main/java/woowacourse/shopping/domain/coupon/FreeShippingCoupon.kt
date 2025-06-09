package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.cart.Receipt
import java.time.LocalDate
import java.time.LocalDateTime

class FreeShippingCoupon(
    override val description: String,
    override val expirationDate: LocalDate,
    val minimumOrderPrice: Int,
) : Coupon {
    override fun isAvailable(receipt: Receipt, current: LocalDateTime): Boolean =
        receipt.totalPrice >= minimumOrderPrice && current.toLocalDate() <= expirationDate

    override fun discountPrice(receipt: Receipt): Int {
        return receipt.shippingPrice
    }
}