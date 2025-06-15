package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.OrderInfo
import java.time.LocalDate

data class FreeShippingCoupon(
    override val code: String,
    override val id: Long,
    override val description: String,
    override val expirationDate: LocalDate,
    val minimumAmount: Int,
    override val discountType: String,
) : Coupon {
    override fun calculateDiscount(orderInfo: OrderInfo): OrderInfo {
        return orderInfo.copy(deliveryAmount = 0, discount = -orderInfo.deliveryAmount)
    }
}
