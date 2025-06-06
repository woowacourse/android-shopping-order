package woowacourse.shopping.domain.coupon

import java.time.LocalDateTime
import java.time.LocalTime

data class Percentage(
    override val description: String,
    override val code: String,
    override val explanationDate: LocalDateTime,
    override val id: Long,
    override val discountType: DiscountType = DiscountType.PERCENTAGE,
    val discount: Int,
    override val availableStartTime: LocalTime? = null,
    override val availableEndTime: LocalTime? = null,
) : Coupon()
