package woowacourse.shopping.domain.model.coupon

import java.time.LocalDate

data class FreeShippingCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    val minimumAmount: Int,
) : Coupon
