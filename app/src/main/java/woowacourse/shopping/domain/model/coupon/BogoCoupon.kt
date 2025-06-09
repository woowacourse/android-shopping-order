package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartProduct
import java.time.LocalDate
import java.time.LocalTime

data class BogoCoupon(
    val buyQuantity: Int,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    val getQuantity: Int,
    override val id: Long,
) : Coupon() {
    override fun isApplicable(
        carts: List<CartProduct>,
        time: LocalTime,
    ): Boolean = carts.any { it.quantity >= 3 }
}
