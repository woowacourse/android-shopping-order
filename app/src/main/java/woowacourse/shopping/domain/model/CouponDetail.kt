package woowacourse.shopping.domain.model

import woowacourse.shopping.domain.model.CouponDiscountType.BUY_X_GET_Y
import woowacourse.shopping.domain.model.CouponDiscountType.FIXED
import woowacourse.shopping.domain.model.CouponDiscountType.FREE_SHIPPING
import woowacourse.shopping.domain.model.CouponDiscountType.PERCENTAGE
import java.time.LocalDate
import java.time.LocalTime

data class CouponDetail(
    val id: Int,
    val code: String,
    val name: String,
    val expirationDate: LocalDate,
    val discount: Int?,
    val minimumPurchase: Int?,
    val discountType: CouponDiscountType,
    val buyQuantity: Int?,
    val getQuantity: Int?,
    val availableTime: AvailableTime?,
) {
    data class AvailableTime(
        val start: LocalTime,
        val end: LocalTime,
    )

    companion object {
        fun CouponDetail.toCoupon(): Coupon =
            when (this.discountType) {
                FIXED -> FixedDiscountCoupon(this)
                PERCENTAGE -> PercentageDiscountCoupon(this)
                FREE_SHIPPING -> FreeShippingCoupon(this)
                BUY_X_GET_Y -> QuantityBonusCoupon(this)
            }
    }
}
