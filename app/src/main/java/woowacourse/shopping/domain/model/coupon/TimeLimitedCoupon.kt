package woowacourse.shopping.domain.model.coupon

import java.time.LocalDate

data class TimeLimitedCoupon(
    val availableTime: LocalDate,
    override val code: String,
    override val description: String,
    val discount: Int,
    override val discountType: String,
    override val expirationDate: String,
    override val id: Long,
) : Coupon()
