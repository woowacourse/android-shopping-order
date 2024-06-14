package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartWithProduct
import java.time.LocalDate

data class ShippingCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    val minimumAmount: Int,
) : Coupon(DiscountType.FreeShipping) {
    override fun canUse(products: List<CartWithProduct>): Boolean {
        val isMoreThanMinimumAmount =
            products.sumOf { it.product.price * it.quantity.value } >= minimumAmount
        val isNotExpired = LocalDate.now().isBefore(expirationDate)
        return isNotExpired && isMoreThanMinimumAmount
    }

    override fun discountPrice(products: List<CartWithProduct>): Int = NO_DISCOUNT

    companion object {
        const val NO_DISCOUNT = 0
    }
}
