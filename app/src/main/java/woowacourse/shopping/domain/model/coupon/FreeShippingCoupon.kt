package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.coupon.Order.Companion.SHIPPING_FEE
import woowacourse.shopping.ui.model.CouponUiModel
import java.time.LocalDate

data class FreeShippingCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    override val discountType: String,
    val minimumAmount: Int,
): Coupon {
    override fun isAvailability(cartItems: List<CartItem>): Boolean {
        val isAmountThreshold = cartItems.sumOf { it.product.price * it.quantity } >= minimumAmount
        val isExpirationDate = LocalDate.now() <= expirationDate
        return isAmountThreshold && isExpirationDate
    }

    override fun calculateDiscountAmount(cartItems: List<CartItem>): Int = -SHIPPING_FEE

    override fun copy(): Coupon =
        FreeShippingCoupon(
            id = id,
            code = code,
            description = description,
            expirationDate = expirationDate,
            discountType = discountType,
            minimumAmount = minimumAmount,
        )

    companion object {
        const val TYPE = "freeShipping"

        fun FreeShippingCoupon.toUiModel() =
            CouponUiModel(
                id = id,
                description = description,
                expirationDate = expirationDate,
                minimumAmount = minimumAmount,
                discountType = discountType,
            )
    }
}
