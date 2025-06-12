package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.OrderInfo
import java.time.LocalDate

data class PercentageCoupon(
    override val code: String,
    override val id: Long,
    override val description: String,
    override val expirationDate: LocalDate,
    val discount: Int,
    val availableTime: AvailableTime,
    override val discountType: String,
) : Coupon {
    override fun calculateDiscount(orderInfo: OrderInfo): OrderInfo {
        val discount = (orderInfo.orderAmount * DISCOUNT_PERCENTAGE).toInt()
        return orderInfo.copy(discount = -discount)
    }

    override fun isAvailable(date: LocalDate): Boolean {
        val isAvailable = availableTime.isAvailableTime()
        return super.isAvailable(date) && isAvailable
    }

    companion object {
        private const val DISCOUNT_PERCENTAGE = 0.3
    }
}
