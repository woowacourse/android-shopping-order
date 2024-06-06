package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartWithProduct
import woowacourse.shopping.domain.model.ProductWithQuantity
import java.time.LocalDate

interface Coupon {
    val id: Long
    val code: String
    val description: String
    val expirationDate: LocalDate
    val discountType: String

    fun canUse(products: List<CartWithProduct>): Boolean

    fun discountPrice(products: List<CartWithProduct>): Int
}
