package woowacourse.shopping.domain.entity.coupon

import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.entity.CartProduct
import java.time.LocalDateTime

data class BOGOCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDateTime,
    val buyCount: Int,
    val freeCount: Int,
) : Coupon(id, code, description, expirationDate) {
    private val discountLimitCount = buyCount + freeCount

    override fun available(
        cart: Cart,
        targetDateTime: LocalDateTime,
    ): Boolean {
        return !isExpired(targetDateTime) && cart.cartProducts.any { it.count >= discountLimitCount }
    }

    override fun calculateDiscount(
        cart: Cart,
        shippingFee: Long,
    ): DiscountResult {
        val discountProducts: List<CartProduct> =
            cart.cartProducts
                .filter { it.count >= discountLimitCount }
        val discountProduct = discountProducts.maxBy { it.product.price }
        val discountPrice = discountProduct.discountPrice()
        return DiscountResult(cart.totalPrice(), discountPrice, shippingFee)
    }

    private fun CartProduct.discountPrice(): Long {
        val discountCount = (count / discountLimitCount) * freeCount
        return product.price.toLong() * discountCount
    }
}
