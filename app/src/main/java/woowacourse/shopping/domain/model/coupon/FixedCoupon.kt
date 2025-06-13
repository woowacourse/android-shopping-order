package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.OrderInfo
import java.time.LocalDate

data class FixedCoupon(
    override val code: String,
    override val id: Long,
    override val description: String,
    override val expirationDate: LocalDate,
    val discount: Int,
    val minimumAmount: Int,
    override val discountType: String,
) : Coupon {
    override fun calculateDiscount(orderInfo: OrderInfo): OrderInfo {
        return orderInfo.copy(discount = -FIXED_DISCOUNT_AMOUNT)
    }

    companion object {
        private const val FIXED_DISCOUNT_AMOUNT = 5000
    }
}
