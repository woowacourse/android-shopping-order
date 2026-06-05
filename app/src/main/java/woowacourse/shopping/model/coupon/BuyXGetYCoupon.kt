package woowacourse.shopping.model.coupon

import woowacourse.shopping.model.CartItem
import woowacourse.shopping.model.Money
import woowacourse.shopping.model.Payment
import java.time.LocalDate

data class BuyXGetYCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    val buyQuantity: Int,
    val getQuantity: Int,
) : Coupon {
    init {
        require(buyQuantity > 0) { "구매 수량은 0 이상이어야 합니다." }
        require(getQuantity > 0) { "구매 시 증정 수량은 0 이상이어야 합니다." }
    }

    override fun isValid(payment: Payment): Boolean =
        payment.nowDate <= expirationDate &&
            payment.cartItems.any { cartItem -> cartItem.quantity >= buyQuantity + getQuantity }

    override fun calculateDiscount(payment: Payment): Money {
        if (!isValid(payment)) return Money(0)

        var discountPrice = 0L

        payment.cartItems.forEach { cartItem ->
            val freeQuantity = calculateFreeQuantity(cartItem)
            val itemPrice = cartItem.product.price.amount * freeQuantity

            discountPrice += itemPrice
        }

        return Money(discountPrice)
    }

    private fun calculateFreeQuantity(cartItem: CartItem): Int {
        val requiredQuantity = buyQuantity + getQuantity
        val applicableSetCount = cartItem.quantity / requiredQuantity

        return applicableSetCount * getQuantity
    }
}
