package woowacourse.shopping.domain.model.coupons

import woowacourse.shopping.domain.model.Cart
import java.time.LocalDate

data class FIXED5000(
    override val id: Long,
    override val code: String = "FIXED5000",
    override val description: String,
    override val expirationDate: LocalDate,
    override val discountType: String,
    override var isChecked: Boolean = false,
    val discount: Int,
    val minimumAmount: Int,
) : Coupon() {
    override fun calculateDiscountRate(carts: List<Cart>): Int {
        return discount
    }

    override fun copy(checked: Boolean): Coupon {
        return FIXED5000(
            id = id,
            code = code,
            description = description,
            expirationDate = expirationDate,
            discountType = discountType,
            discount = discount,
            minimumAmount = minimumAmount,
            isChecked = checked,
        )
    }
}
