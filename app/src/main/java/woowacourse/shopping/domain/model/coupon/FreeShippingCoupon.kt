package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.ui.model.OrderInformation.Companion.SHIPPING_FEE
import java.time.LocalDate

data class FreeShippingCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    override val discountType: String,
    val minimumAmount: Int,
) : Coupon {
    override fun isAvailability(cartItems: List<CartItem>): Boolean {
        val isAmountThreshold = cartItems.sumOf { it.product.price * it.quantity } >= minimumAmount
        val isExpirationDate = LocalDate.now() <= expirationDate
        return isAmountThreshold && isExpirationDate
    }

    override fun calculateDiscountAmount(cartItems: List<CartItem>): Int = -SHIPPING_FEE

    companion object {
        const val TYPE = "freeShipping"
    }
}
