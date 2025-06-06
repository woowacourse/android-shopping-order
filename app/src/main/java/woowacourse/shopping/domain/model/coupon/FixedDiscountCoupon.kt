package woowacourse.shopping.domain.model.coupon

import java.time.LocalDate

data class FixedDiscountCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    val discount: Int,
    val minimumAmount: Int,
) : Coupon
