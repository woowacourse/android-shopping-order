package woowacourse.shopping.domain.model.coupons

import woowacourse.shopping.domain.model.Cart
import java.time.LocalDate

data class FREESHIPPING(
    override val id: Long,
    override val code: String = "FREESHIPPING",
    override val description: String,
    override val expirationDate: LocalDate,
    override val discountType: String,
    val minimumAmount: Int,
) : Coupon {
    override fun calculateDiscountRate(carts: List<Cart>): Int {
        return 3000
    }
}
