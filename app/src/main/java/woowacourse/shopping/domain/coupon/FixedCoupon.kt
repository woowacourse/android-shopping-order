package woowacourse.shopping.domain.coupon

import java.time.LocalDate

data class FixedCoupon(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val discountType: String,
    override val expirationDate: LocalDate,
    val minimumAmount: Int,
    val discount: Int,
) : Coupon
