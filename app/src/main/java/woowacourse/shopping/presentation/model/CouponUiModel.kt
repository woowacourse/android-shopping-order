package woowacourse.shopping.presentation.model

import woowacourse.shopping.domain.model.AvailableTime
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.CouponCondition
import woowacourse.shopping.domain.model.coupon.DiscountPolicy
import java.util.Date

data class CouponUiModel(
    val id: Int,
    val description: String,
    val expirationDate: Date,
    val minimumAmount: Int? = null,
    val buyQuantity: Int? = null,
    val getQuantity: Int? = null,
    val availableTime: AvailableTime? = null,
    val discountPolicy: DiscountPolicy,
    val couponCondition: CouponCondition,
    val checked: Boolean = false,
)

fun Coupon.toPresentation(): CouponUiModel {
    return CouponUiModel(
        id = id,
        description = description,
        expirationDate = expirationDate,
        minimumAmount = minimumAmount,
        buyQuantity = buyQuantity,
        getQuantity = getQuantity,
        availableTime = availableTime,
        discountPolicy = discountPolicy,
        couponCondition = couponCondition,
    )
}
