package woowacourse.shopping.domain.coupon

import java.time.LocalDate

data class BuyXGetY(
    override val description: String,
    override val code: String,
    override val explanationDate: LocalDate,
    override val id: Long,
    override val discountType: DiscountType = DiscountType.BUY_X_GET_Y,
    val buyQuantity: Int,
    val getQuantity: Int,
) : Coupon()
