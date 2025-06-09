package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.AvailableTime
import woowacourse.shopping.domain.model.CartProduct
import java.time.LocalDate
import java.time.LocalTime

data class TimeLimitedCoupon(
    val availableTime: AvailableTime,
    override val code: String,
    override val description: String,
    val discount: Int,
    override val expirationDate: LocalDate,
    override val id: Long,
) : Coupon() {
    override fun isApplicable(
        carts: List<CartProduct>,
        time: LocalTime,
    ): Boolean = availableTime.isAvailable(time)
}
