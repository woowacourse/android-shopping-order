package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.AvailableTime
import java.time.LocalDate

data class TimeLimitedCoupon(
    val availableTime: AvailableTime,
    override val code: String,
    override val description: String,
    val discount: Int,
    override val expirationDate: LocalDate,
    override val id: Long,
) : Coupon()
