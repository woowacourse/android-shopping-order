package woowacourse.shopping.domain.model.coupon

import java.time.LocalDate

data class FreeShippingCoupon(
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    override val id: Long,
    val minimumAmount: Int,
) : Coupon()
