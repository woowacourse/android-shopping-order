package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.remote.retrofit.dto.CouponItem
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.CouponBenefit

fun List<CouponItem>.toDomainCoupons(): List<Coupon> = map { coupon -> coupon.toDomainCoupon() }

private fun CouponItem.toDomainCoupon(): Coupon =
    Coupon(
        id = id,
        code = code,
        description = description,
        expirationDate = expirationDate,
        benefit = toDomainCouponBenefit(),
    )

private fun CouponItem.toDomainCouponBenefit(): CouponBenefit {
    val normalizedCode = code.uppercase()
    val normalizedType = discountType.uppercase()
    return when {
        normalizedType == TYPE_FIXED || normalizedCode.startsWith(FIXED_PREFIX) ->
            CouponBenefit.AmountDiscount(
                discountAmount = discount,
                minimumOrderAmount = minimumAmount,
            )

        normalizedType == TYPE_BUY_X_GET_Y ||
            normalizedType == TYPE_BOGO ||
            normalizedCode.startsWith(BOGO_PREFIX) ||
            normalizedCode.startsWith(BUY_PREFIX) ->
            CouponBenefit.BuyTwoGetOne(
                requiredQuantity = buyQuantity,
                freeQuantity = getQuantity,
            )

        normalizedType == TYPE_FREE_SHIPPING || normalizedCode.startsWith(FREE_SHIPPING_PREFIX) ->
            CouponBenefit.FreeShipping(
                minimumOrderAmount = minimumAmount,
            )

        normalizedType == TYPE_PERCENTAGE || normalizedCode.startsWith(PERCENT_PREFIX) || normalizedCode == MIRACLESALE ->
            CouponBenefit.MorningDiscount(
                startTime = availableTime?.start ?: DEFAULT_START_TIME,
                endTime = availableTime?.end ?: DEFAULT_END_TIME,
                discountRate = discount,
            )

        else ->
            CouponBenefit.Unknown(
                discountType = discountType,
                discount = discount,
                minimumOrderAmount = minimumAmount,
            )
    }
}

private const val TYPE_FIXED = "FIXED"
private const val TYPE_BOGO = "BOGO"
private const val TYPE_BUY_X_GET_Y = "BUYXGETY"
private const val TYPE_FREE_SHIPPING = "FREESHIPPING"
private const val TYPE_PERCENTAGE = "PERCENTAGE"
private const val FIXED_PREFIX = "FIXED"
private const val BOGO_PREFIX = "BOGO"
private const val BUY_PREFIX = "BUY"
private const val FREE_SHIPPING_PREFIX = "FREESHIPPING"
private const val PERCENT_PREFIX = "PERCENT"
private const val MIRACLESALE = "MIRACLESALE"
private const val DEFAULT_START_TIME = "00:00:00"
private const val DEFAULT_END_TIME = "23:59:59"
