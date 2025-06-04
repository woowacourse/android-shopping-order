package woowacourse.shopping.domain.coupon

import java.time.LocalDate

data class BogoCoupon(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val discountType: String,
    override val expirationDate: LocalDate,
    val buyQuantity: Int,
    val getQuantity: Int,
) : Coupon
