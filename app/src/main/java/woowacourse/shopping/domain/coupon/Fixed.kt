package woowacourse.shopping.domain.coupon

import java.time.LocalDate

data class Fixed(
    override val description: String,
    override val code: String,
    override val explanationDate: LocalDate,
    override val id: Long,
    override val discountType: DiscountType = DiscountType.FIXED,
    val discount: Int,
    override val minimumAmount: Int?,
) : Coupon()
