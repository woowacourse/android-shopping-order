package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.ui.model.CouponUiModel
import java.time.LocalDate

data class BuyXGetYCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    override val discountType: String,
    val buyQuantity: Int,
    val getQuantity: Int,
): Coupon {
    override fun isAvailability(cartItems: List<CartItem>): Boolean {
        val hasQuantityOfTwo = cartItems.any { it.quantity == buyQuantity }
        val isExpirationDate = LocalDate.now() <= expirationDate
        return hasQuantityOfTwo && isExpirationDate
    }

    override fun calculateDiscountAmount(cartItems: List<CartItem>): Int {
        val discountTarget: CartItem = cartItems.filter { it.quantity % buyQuantity == 0 }.maxBy { it.product.price }
        return -discountTarget.product.price
    }

    override fun copy(): Coupon =
        BuyXGetYCoupon(
            id = id,
            code = code,
            description = description,
            expirationDate = expirationDate,
            discountType = discountType,
            buyQuantity = buyQuantity,
            getQuantity = getQuantity,
        )

    companion object {
        const val TYPE = "buyXgetY"
    }
}
