package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartItem

class BuyXgetYCoupon(override val coupon: Coupon, override val cartItems: List<CartItem>) :
    CouponState {
    override fun isValid(): Boolean {
        return true
    }

    override fun discountPrice(): Int {
        val cartItemsWithThreeOrMoreQuantity =
            cartItems.filter { cartItem -> cartItem.quantity >= 3 }

        return cartItemsWithThreeOrMoreQuantity.maxOf { cartItem -> cartItem.product.price }
    }
}