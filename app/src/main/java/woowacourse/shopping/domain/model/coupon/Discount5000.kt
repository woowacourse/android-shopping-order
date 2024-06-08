package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartWithProduct
import java.time.LocalDate

data class Discount5000(
    override val id: Long,
    override val description: String,
    override val expirationDate: LocalDate,
    val type: String,
    val discount: Int,
    val minimumAmount: Int,
    override val code: String,
) : Coupon(type) {
    override fun canUse(products: List<CartWithProduct>): Boolean {
        val isMoreThanMinimumAmount =
            products.sumOf { it.product.price * it.quantity.value } >= minimumAmount
        val isNotExpired = LocalDate.now().isBefore(expirationDate)
        return isMoreThanMinimumAmount && isNotExpired
    }

    override fun discountPrice(products: List<CartWithProduct>): Int {
        if (!canUse(products)) error("$description 쿠폰을 적용할 수 없습니다.")
        return discount
    }
}
