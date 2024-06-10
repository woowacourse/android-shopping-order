package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.AvailableTime
import java.util.Date

data class Coupon(
    val id: Int,
    val description: String,
    val expirationDate: Date,
    val discount: Int? = null,
    val minimumAmount: Int? = null,
    val buyQuantity: Int? = null,
    val getQuantity: Int? = null,
    val availableTime: AvailableTime? = null,
    val discountPolicy: DiscountPolicy,
    val couponCondition: CouponCondition,
)
