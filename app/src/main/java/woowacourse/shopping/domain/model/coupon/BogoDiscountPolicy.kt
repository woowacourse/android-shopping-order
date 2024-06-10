package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.Cart

class BogoDiscountPolicy(private val buyQuantity: Int?, private val getQuantity: Int?) :
    DiscountPolicy {
    private fun findDiscountCart(carts: List<Cart>): Cart? {
        buyQuantity ?: return null
        return carts.filter { cart -> cart.quantity >= buyQuantity }
            .maxByOrNull { it.product.price }
    }

    override fun calculateDiscount(
        totalAmount: Int,
        carts: List<Cart>,
    ): Int {
        val discountCart = findDiscountCart(carts) ?: return 0
        getQuantity ?: return 0
        buyQuantity ?: return 0

        val price = discountCart.product.price
        val discountCartQuantity = discountCart.quantity
        return (discountCartQuantity / (buyQuantity + getQuantity)) * getQuantity * price
    }
}
