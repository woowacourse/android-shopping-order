package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.Cart
import java.time.Instant
import java.util.Date

class MinimumPurchaseAmountCondition(
    private val expirationDate: Date?,
    private val minimumAmount: Int?,
) : CouponCondition {
    override fun isValid(carts: List<Cart>): Boolean {
        expirationDate ?: return false
        minimumAmount ?: return false

        if (expirationDate.before(Date.from(Instant.now()))) return false
        val totalAmount = carts.sumOf { it.totalPrice }
        return minimumAmount <= totalAmount
    }
}
