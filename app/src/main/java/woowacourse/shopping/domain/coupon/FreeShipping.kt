package woowacourse.shopping.domain.coupon

import java.time.LocalDateTime

class FreeShipping(
    override val description: String,
    override val code: String,
    override val explanationDate: LocalDateTime,
    override val id: Long,
    override val discountType: DiscountType = DiscountType.FREE_SHIPPING,
    override val minimumAmount: Int?,
) : Coupon()
