package woowacourse.shopping.domain.model.coupon

import java.time.LocalDate

data class BogoCoupon(
    val buyQuantity: Int,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    val getQuantity: Int,
    override val id: Long,
) : Coupon()
