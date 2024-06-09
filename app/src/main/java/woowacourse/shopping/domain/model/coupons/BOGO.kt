package woowacourse.shopping.domain.model.coupons

import woowacourse.shopping.domain.model.Cart
import java.time.LocalDate

data class BOGO(
    override val id: Long,
    override val code: String = "BOGO",
    override val description: String,
    override val expirationDate: LocalDate,
    override val discountType: String,
    override var isChecked: Boolean = false,
    val buyQuantity: Int,
    val getQuantity: Int,
) : Coupon() {
    override fun calculateDiscountRate(carts: List<Cart>): Int {
        val discountTargetCart = carts.maxBy { it.totalPrice }

        return discountTargetCart.product.price * getQuantity
    }

    override fun copy(checked: Boolean): Coupon {
        return BOGO(
            id = id,
            code = code,
            description = description,
            expirationDate = expirationDate,
            discountType = discountType,
            buyQuantity = buyQuantity,
            getQuantity = getQuantity,
            isChecked = checked,
        )
    }
}
