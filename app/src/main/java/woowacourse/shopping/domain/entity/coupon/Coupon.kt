package woowacourse.shopping.domain.entity.coupon

import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.entity.CartProduct
import java.time.LocalDateTime

sealed class Coupon(
    open val id: Long,
    open val code: String,
    open val description: String,
    open val discountableMinPrice: Long,
    open val expirationDate: LocalDateTime,
) {
    protected fun isExpired(targetDateTime: LocalDateTime): Boolean =
        expirationDate.isBefore(targetDateTime)

    abstract fun available(cart: Cart, targetDateTime: LocalDateTime): Boolean

    fun discount(
        cart: Cart,
        shippingFee: Long,
        targetDateTime: LocalDateTime = LocalDateTime.now(),
    ): DiscountResult {
        return if (available(cart, targetDateTime)) {
            calculateDiscount(cart, shippingFee)
        } else {
            noDiscount(cart, shippingFee)
        }
    }

    fun discount(
        orderProducts: List<CartProduct>,
        shippingFee: Long,
    ): DiscountResult {
        return discount(Cart(orderProducts), shippingFee)
    }

    fun discount(
        orderProduct: CartProduct,
        shippingFee: Long,
    ): DiscountResult {
        return discount(Cart(orderProduct), shippingFee)
    }

    private fun noDiscount(
        cart: Cart,
        shippingFee: Long,
    ): DiscountResult {
        return DiscountResult(cart.totalPrice(), 0, shippingFee)
    }

    protected abstract fun calculateDiscount(
        cart: Cart,
        shippingFee: Long,
    ): DiscountResult
}
