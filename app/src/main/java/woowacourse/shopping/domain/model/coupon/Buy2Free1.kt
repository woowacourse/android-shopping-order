package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartWithProduct
import java.time.LocalDate

data class Buy2Free1(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    val buyQuantity: Int,
    val getQuantity: Int,
) : Coupon(DiscountType.BuyXGetY) {
    private val applyCount = buyQuantity + getQuantity

    override fun canUse(products: List<CartWithProduct>): Boolean {
        val isProductWithQuantity3 = products.any { it.quantity.value == applyCount }
        val isNotExpired = LocalDate.now().isBefore(expirationDate)
        return isProductWithQuantity3 && isNotExpired
    }

    override fun discountPrice(products: List<CartWithProduct>): Int {
        if (!canUse(products)) error("$description 쿠폰을 적용할 수 없습니다.")
        val productsQuantity3 = products.filter { it.quantity.value == applyCount }
        return productsQuantity3.maxOf { it.product.price }
    }
}
