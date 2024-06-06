package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.ProductWithQuantity
import java.time.LocalDate

data class Discount5000(
    override val id: Long,
    override val description: String,
    override val discountType: String,
    override val expirationDate: LocalDate,
    val discount: Int,
    val minimumAmount: Int,
    override val code: String,
) : Coupon {

    override fun canUse(products: List<ProductWithQuantity>): Boolean {
        val isMoreThanMinimumAmount = products.sumOf { it.totalPrice } >= minimumAmount
        val isNotExpired = LocalDate.now().isBefore(expirationDate)
        return isMoreThanMinimumAmount && isNotExpired
    }

    override fun discountPrice(products: List<ProductWithQuantity>): Int {
        if (!canUse(products)) error("$description 쿠폰을 적용할 수 없습니다.")
        return discount
    }

}
