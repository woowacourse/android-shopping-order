package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartWithProduct
import woowacourse.shopping.domain.model.coupon.DiscountType.Companion.toDiscountType
import java.time.LocalDate

sealed class Coupon(val discountType: DiscountType) {

    constructor(discountType: String) : this(discountType.toDiscountType())


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
    Percentage;

    companion object {
        fun String.toDiscountType(): DiscountType = when (this) {
            "fixed" -> Fixed
            "buyXgetY" -> BuyXGetY
            "freeShipping" -> FreeShipping
            "percentage" -> Percentage
            else -> throw IllegalStateException("해당 타입에 해당하는 쿠폰이 없습니다.")
        }
    }
}
