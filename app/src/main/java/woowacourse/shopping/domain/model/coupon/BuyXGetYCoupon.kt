package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartProduct

data class BuyXGetYCoupon(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val expirationDate: String,
    val buyQuantity: Int,
    val getQuantity: Int,
) : Coupon {
    override fun isValid(items: List<CartProduct>): Boolean {
        val requireQuantity = buyQuantity + getQuantity

        return items.any { it.quantity >= requireQuantity }
    }

    override fun calculateDiscountAmount(items: List<CartProduct>): Int {
        val requireQuantity = buyQuantity + getQuantity
        val filteredItems = items.filter { it.quantity >= requireQuantity }
        val mostExpensiveItem = filteredItems.maxByOrNull { it.product.price } ?: return 0
        return -1 * mostExpensiveItem.product.price * getQuantity
    }
}
