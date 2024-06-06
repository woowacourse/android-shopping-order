package woowacourse.shopping.domain.model.coupon

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
    override fun canUse(products: List<ProductWithQuantity>): Boolean {
        val isMoreThanMinimumAmount = products.sumOf { it.totalPrice } >= minimumAmount
        val isNotExpired = LocalDate.now().isAfter(expirationDate)
        return isNotExpired && isMoreThanMinimumAmount
    }

    override fun discountPrice(products: List<ProductWithQuantity>): Int = NO_DISCOUNT

    companion object {
        const val NO_DISCOUNT = 0
    }

}
