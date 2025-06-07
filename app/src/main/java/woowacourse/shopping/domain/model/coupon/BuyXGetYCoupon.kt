package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartProduct

class BuyXGetYCoupon(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val expirationDate: String,
    val buyQuantity: Int,
    val getQuantity: Int,
) : Coupon {
    override fun isValid(items: List<CartProduct>): Boolean {
        val requireQuantity = buyQuantity + getQuantity
        val productQuantityMap =
            items.groupingBy { it.product.id }
                .eachCount()

        return productQuantityMap.any { (_, quantity) -> quantity >= requireQuantity }
    }
}
