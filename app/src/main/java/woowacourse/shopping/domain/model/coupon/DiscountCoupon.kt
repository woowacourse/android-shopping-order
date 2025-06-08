package woowacourse.shopping.domain.model.coupon

import java.time.LocalDate

data class DiscountCoupon(
    override val code: String,
    override val description: String,
    val discount: Int,
    override val expirationDate: LocalDate,
    override val id: Long,
    val minimumAmount: Int,
) : Coupon()
