package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.OrderInfo
import java.time.LocalDate

data class BuyXGetYCoupon(
    override val code: String,
    override val id: Long,
    override val description: String,
    override val expirationDate: LocalDate,
    val buyQuantity: Int,
    val getQuantity: Int,
    override val discountType: String,
) : Coupon {
    override fun calculateDiscount(orderInfo: OrderInfo): OrderInfo {
        val availableProducts =
            orderInfo.orderProducts
                .filter { it.quantity >= buyQuantity + getQuantity }

        if (availableProducts.isEmpty()) return orderInfo

        val maxPricedProduct =
            availableProducts.maxByOrNull { it.price }
                ?: return orderInfo

        val discount = maxPricedProduct.price

        return orderInfo.copy(discount = -discount)
    }
}
