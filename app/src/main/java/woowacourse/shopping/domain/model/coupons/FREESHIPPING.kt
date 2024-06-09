package woowacourse.shopping.domain.model.coupons

import woowacourse.shopping.domain.model.Cart
import java.time.LocalDate

data class FREESHIPPING(
    override val id: Long,
    override val code: String = "FREESHIPPING",
    override val description: String,
    override val expirationDate: LocalDate,
    override val discountType: String,
    val minimumAmount: Int,
    override var isChecked: Boolean = false,
) : Coupon() {
    override fun calculateDiscountRate(carts: List<Cart>): Int {
        return 3000
    }

    override fun copy(checked: Boolean): Coupon {
        return FREESHIPPING(
            id = id,
            code = code,
            description = description,
            expirationDate = expirationDate,
            discountType = discountType,
            minimumAmount = minimumAmount,
            isChecked = checked,
        )
    }
}
