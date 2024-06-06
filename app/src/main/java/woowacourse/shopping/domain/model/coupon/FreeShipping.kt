package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartWithProduct
import woowacourse.shopping.domain.model.ProductWithQuantity
import java.time.LocalDate

data class FreeShipping(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    override val discountType: String,
    val minimumAmount: Int,
) : Coupon {
    override fun canUse(products: List<CartWithProduct>): Boolean {
        val isMoreThanMinimumAmount = products.sumOf { it.product.price * it.quantity.value  } >= minimumAmount
        val isNotExpired = LocalDate.now().isAfter(expirationDate)
        return isNotExpired && isMoreThanMinimumAmount
    }

    override fun discountPrice(products: List<CartWithProduct>): Int = NO_DISCOUNT

    companion object {
        const val NO_DISCOUNT = 0
    }

}
