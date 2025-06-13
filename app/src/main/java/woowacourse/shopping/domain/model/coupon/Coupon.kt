package woowacourse.shopping.domain.model.coupon

import java.time.LocalDate
import java.time.LocalTime
import woowacourse.shopping.domain.model.CartItem

sealed class Coupon {
    abstract val couponBase: CouponBase

    protected fun isExpired(now: LocalDate = LocalDate.now()): Boolean = now.isAfter(couponBase.expirationDate)

    abstract fun isAvailable(cartItems: List<CartItem>): Boolean

    abstract fun getDiscountPrice(cartItems: List<CartItem>): Int

    abstract fun getDiscountDeliveryFee(): Int
}

data class CouponFixedDiscount(
    override val couponBase: CouponBase,
    val discountPrice: Int,
    val minimumOrderPrice: Int,
    val couponDiscountType: CouponDiscountType,
) : Coupon() {
    override fun isAvailable(cartItems: List<CartItem>): Boolean =
        !isExpired() && minimumOrderPrice <= cartItems.sumOf { cartItem -> cartItem.totalPrice }

    override fun getDiscountPrice(cartItems: List<CartItem>): Int = discountPrice

    override fun getDiscountDeliveryFee(): Int = 0
}

data class CouponBuyXGetY(
    override val couponBase: CouponBase,
    val buyQuantity: Int,
    val getQuantity: Int,
    val discountType: CouponDiscountType,
) : Coupon() {
    override fun isAvailable(cartItems: List<CartItem>): Boolean =
        !isExpired() &&
            cartItems.any { cartItem -> cartItem.quantity >= buyQuantity + getQuantity }

    override fun getDiscountPrice(cartItems: List<CartItem>): Int =
        cartItems
            .filter { cartItem -> cartItem.quantity >= buyQuantity + getQuantity }
            .maxOf { cartItem -> cartItem.product.price }

    override fun getDiscountDeliveryFee(): Int = 0
}

data class CouponFreeShipping(
    override val couponBase: CouponBase,
    val minimumOrderPrice: Int,
    val discountType: CouponDiscountType,
) : Coupon() {
    override fun isAvailable(cartItems: List<CartItem>): Boolean =
        !isExpired() && minimumOrderPrice <= cartItems.sumOf { cartItem -> cartItem.totalPrice }

    override fun getDiscountPrice(cartItems: List<CartItem>): Int = 0

    override fun getDiscountDeliveryFee(): Int = DEFAULT_DELIVERY_FEE

    companion object {
        private const val DEFAULT_DELIVERY_FEE = 3_000
    }
}

data class CouponPercentDiscount(
    override val couponBase: CouponBase,
    val discountPercent: Int,
    val availableStartTime: LocalTime,
    val availableEndTime: LocalTime,
    val discountType: CouponDiscountType,
) : Coupon() {
    override fun isAvailable(cartItems: List<CartItem>): Boolean {
        val nowTime = LocalTime.now()
        return !isExpired() &&
            nowTime.isAfter(availableStartTime) &&
            nowTime.isBefore(availableEndTime)
    }

    override fun getDiscountPrice(cartItems: List<CartItem>): Int {
        val total = cartItems.sumOf { it.totalPrice }
        return (total * (discountPercent / 100.0)).toInt()
    }

    override fun getDiscountDeliveryFee(): Int = 0
}
