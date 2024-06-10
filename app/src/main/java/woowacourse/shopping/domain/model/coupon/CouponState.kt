package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.AvailableTime
import woowacourse.shopping.domain.model.CartItem
import java.util.Date

abstract class CouponState {
    abstract val coupon: Coupon

    fun copy(
        id: Int = this.coupon.id,
        description: String = this.coupon.description,
        expirationDate: Date = this.coupon.expirationDate,
        discount: Int? = this.coupon.discount,
        minimumAmount: Int? = this.coupon.minimumAmount,
        buyQuantity: Int? = this.coupon.buyQuantity,
        getQuantity: Int? = this.coupon.getQuantity,
        availableTime: AvailableTime? = this.coupon.availableTime,
        discountType: String = this.coupon.discountType,
        checked: Boolean = this.coupon.checked,
    ): CouponState {
        val newCoupon =
            this.coupon.copy(
                id = id,
                description = description,
                expirationDate = expirationDate,
                discount = discount,
                minimumAmount = minimumAmount,
                buyQuantity = buyQuantity,
                getQuantity = getQuantity,
                availableTime = availableTime,
                discountType = discountType,
                checked = checked,
            )
        return createState(newCoupon)
    }

    protected abstract fun createState(coupon: Coupon): CouponState

    abstract fun isValidCoupon(carts: List<CartItem>): Boolean

    abstract fun calculateDiscount(totalAmount: Int): Int
}
