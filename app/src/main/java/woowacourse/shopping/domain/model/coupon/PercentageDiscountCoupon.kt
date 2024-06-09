package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.ui.model.CouponUiModel
import java.time.LocalDate
import java.time.LocalTime

data class PercentageDiscountCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    override val discountType: String,
    val discount: Int,
    val availableTime: AvailableTime,
): Coupon {
    override fun isAvailability(cartItems: List<CartItem>): Boolean {
        val isAvailableTime = LocalTime.now() in availableTime.start..availableTime.end
        val isExpirationDate = LocalDate.now() <= expirationDate
        return isAvailableTime && isExpirationDate
    }

    companion object {
        fun PercentageDiscountCoupon.toUiModel() =
            CouponUiModel(
                id = id,
                description = description,
                expirationDate = expirationDate,
                minimumAmount = null,
                discountType = discountType,
            )
    }
}
