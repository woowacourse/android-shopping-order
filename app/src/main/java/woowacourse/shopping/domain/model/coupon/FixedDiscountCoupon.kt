package woowacourse.shopping.domain.model.coupon

import android.util.Log
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.ui.model.CouponUiModel
import java.time.LocalDate

data class FixedDiscountCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    override val discountType: String,
    val discount: Int,
    val minimumAmount: Int,
): Coupon {
    override fun isAvailability(cartItems: List<CartItem>): Boolean {
        val isAmountThreshold = cartItems.sumOf { it.product.price * it.quantity } >= minimumAmount
        val isExpirationDate = LocalDate.now() <= expirationDate
        return isAmountThreshold && isExpirationDate
    }

    companion object {
        fun FixedDiscountCoupon.toUiModel() =
            CouponUiModel(
                id = id,
                description = description,
                expirationDate = expirationDate,
                minimumAmount = minimumAmount,
                discountType = discountType,
            )
    }
}
