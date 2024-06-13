package woowacourse.shopping.data.coupon.remote

import woowacourse.shopping.data.util.toLocalDate
import woowacourse.shopping.data.util.toLocalTime
import woowacourse.shopping.domain.model.AvailableTime
import woowacourse.shopping.domain.model.Quantity
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.condition.AmountDiscountCondition
import woowacourse.shopping.domain.model.coupon.condition.DiscountCondition
import woowacourse.shopping.domain.model.coupon.condition.QuantityDiscountCondition
import woowacourse.shopping.domain.model.coupon.condition.TimeDiscountCondition
import woowacourse.shopping.domain.model.coupon.policy.DiscountPolicy
import woowacourse.shopping.domain.model.coupon.policy.FixedDiscountPolicy
import woowacourse.shopping.domain.model.coupon.policy.FreeQuantityDiscountPolicy
import woowacourse.shopping.domain.model.coupon.policy.FreeShippingDiscountPolicy
import woowacourse.shopping.domain.model.coupon.policy.PercentDiscountPolicy
import java.lang.IllegalArgumentException

private const val INVALID_DISCOUNT_PARAMETER = "CouponDto의 discount 파라미터가 null 입니다."
private const val INVALID_BUY_QUANTITY_PARAMETER = "CouponDto의 buyQuantity 파라미터가 null 입니다."
private const val INVALID_GET_QUANTITY_PARAMETER = "CouponDto의 getQuantity 파라미터가 null 입니다."

fun List<CouponDto>.toCoupons(): List<Coupon> = map { it.toCoupon() }

fun CouponDto.toCoupon(): Coupon {
    return Coupon(
        id,
        description,
        expirationDate.toLocalDate(),
        minimumAmount,
        this.toDiscountPolicy(),
    )
}

fun CouponDto.toDiscountPolicy(): DiscountPolicy {
    val discountConditions = this.toDiscountConditions()
    val discountType = DiscountType.from(discountType)

    return when (discountType) {
        DiscountType.FIXED -> this.toFixedDiscountPolicy(discountConditions)
        DiscountType.BUY_X_GET_Y -> this.toFreeQuantityDiscountPolicy(discountConditions)
        DiscountType.FREE_SHIPPING -> this.toFreeShippingDiscountPolicy(discountConditions)
        DiscountType.PERCENTAGE -> this.toPercentDiscountPolicy(discountConditions)
    }
}

private fun CouponDto.toDiscountConditions(): List<DiscountCondition> {
    val discountConditions = mutableListOf<DiscountCondition>()
    if (minimumAmount != null) {
        discountConditions.add(AmountDiscountCondition(minimumAmount))
    }
    if (buyQuantity != null && getQuantity != null) {
        discountConditions.add(QuantityDiscountCondition(buyQuantity + getQuantity))
    }
    if (availableTime != null) {
        discountConditions.add(TimeDiscountCondition(availableTime.toAvailableTime()))
    }
    return discountConditions
}

private fun CouponDto.toFixedDiscountPolicy(discountConditions: List<DiscountCondition>): FixedDiscountPolicy {
    return FixedDiscountPolicy(
        discountConditions,
        discount ?: throw IllegalArgumentException(INVALID_DISCOUNT_PARAMETER),
    )
}

private fun CouponDto.toFreeQuantityDiscountPolicy(discountConditions: List<DiscountCondition>): FreeQuantityDiscountPolicy {
    val buyQuantity = buyQuantity ?: throw IllegalArgumentException(INVALID_BUY_QUANTITY_PARAMETER)
    val getQuantity = getQuantity ?: throw IllegalArgumentException(INVALID_GET_QUANTITY_PARAMETER)
    val minimumQuantity = buyQuantity + getQuantity
    return FreeQuantityDiscountPolicy(
        discountConditions,
        Quantity(minimumQuantity),
        Quantity(getQuantity),
    )
}

private fun CouponDto.toFreeShippingDiscountPolicy(discountConditions: List<DiscountCondition>): FreeShippingDiscountPolicy {
    return FreeShippingDiscountPolicy(discountConditions)
}

private fun CouponDto.toPercentDiscountPolicy(discountConditions: List<DiscountCondition>): PercentDiscountPolicy {
    return PercentDiscountPolicy(
        discountConditions,
        discount ?: throw IllegalArgumentException(INVALID_DISCOUNT_PARAMETER),
    )
}

private fun AvailableTimeDto.toAvailableTime(): AvailableTime {
    return AvailableTime(start.toLocalTime(), end.toLocalTime())
}
