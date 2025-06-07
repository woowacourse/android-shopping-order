package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartProduct
import java.time.LocalTime

class PercentageCoupon(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val expirationDate: String,
    val discount: Int,
    val availableTime: AvailableTime,
) : Coupon {
    override fun isValid(items: List<CartProduct>): Boolean {
        return availableTime.isInRange(LocalTime.now())
    }
}
