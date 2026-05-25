package woowacourse.shopping.model

import woowacourse.shopping.model.coupon.Coupon
import java.time.LocalDate
import java.time.LocalTime

data class Payment(
    val cartItems: List<CartItem>,
    val selectedCoupon: Coupon? = null,
    val nowDate: LocalDate = LocalDate.now(),
    val nowTime: LocalTime = LocalTime.now(),
) {
    val totalPrice: Money
        get() = cartItems.fold(Money(0)) { acc, cartItem -> acc + cartItem.getTotalPrice() }

    val deliveryFee: Money
        get() = Money(DEFAULT_DELIVERY_FEE)

    val discountPrice: Money
        get() = selectedCoupon?.calculateDiscount(this) ?: Money(0)

    val finalPrice: Money
        get() = totalPrice + deliveryFee - discountPrice

    fun availableCoupons(coupons: List<Coupon>): List<Coupon> = coupons.filter { it.isValid(this) }

    companion object {
        private const val DEFAULT_DELIVERY_FEE = 3_000L
    }
}
