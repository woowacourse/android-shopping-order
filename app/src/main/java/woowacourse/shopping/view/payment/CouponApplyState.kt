package woowacourse.shopping.view.payment

import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.payment.Coupon

data class CouponApplyState(
    val loading: Boolean,
    val coupons: List<CouponsItem>,
    val selectedCoupon: Coupon?,
    val cartItems: List<CartItem>,
    val deliveryFee: Int,
) {
    val orderAmount: Int = cartItems.sumOf(CartItem::price)
    val discountAmount: Int =
        when (selectedCoupon) {
            is Coupon.BuyNGetNCoupon ->
                cartItems
                    .filter { it.quantity >= 3 }
                    .maxByOrNull { cartItem: CartItem ->
                        cartItem.productPrice
                    }?.productPrice ?: 0

            is Coupon.FixedDiscountCoupon -> selectedCoupon.discount
            is Coupon.FreeShippingCoupon -> deliveryFee
            is Coupon.PercentageCoupon -> (orderAmount * selectedCoupon.discountRate).toInt()
            null -> 0
        }
    val totalPaymentAmount: Int = orderAmount - discountAmount

    companion object {
        val Loading: CouponApplyState =
            CouponApplyState(
                loading = true,
                coupons = emptyList(),
                selectedCoupon = null,
                cartItems = emptyList(),
                deliveryFee = 0,
            )
    }
}
