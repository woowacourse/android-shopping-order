package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartWithProduct
import java.time.LocalDate

sealed class Coupon(val discountType: DiscountType) {

    abstract val id: Long
    abstract val code: String
    abstract val description: String
    abstract val expirationDate: LocalDate

    abstract fun canUse(products: List<CartWithProduct>): Boolean

    abstract fun discountPrice(products: List<CartWithProduct>): Int
}

enum class DiscountType {
    BuyXGetY,
    FreeShipping,
    Fixed,
    Percentage,
}
