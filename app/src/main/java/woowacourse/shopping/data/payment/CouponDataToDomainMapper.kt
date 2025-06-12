package woowacourse.shopping.data.payment

import android.util.Log
import woowacourse.shopping.domain.payment.Coupon
import woowacourse.shopping.domain.payment.Coupon.PercentageCoupon.AvailableTime
import java.time.LocalDate
import java.time.LocalTime

fun CouponDataModel.toDomain(): Coupon? =
    when (discountType) {
        "fixed" -> FixedDiscountCoupon()
        "buyXgetY" -> BuyNGetNCoupon()
        "freeShipping" -> FreeShippingCoupon()
        "percentage" -> PercentageCoupon()
        else -> {
            Log.e("TAG", "No Such Coupon, discountType: $discountType")
            error("No Such Coupon")
        }
    }

private fun CouponDataModel.FixedDiscountCoupon(): Coupon.FixedDiscountCoupon? {
    if (discount == null || minimumAmount == null) {
        return null
    }

    return Coupon.FixedDiscountCoupon(
        id = id,
        code = code,
        description = description,
        expirationDate = LocalDate.parse(expirationDate),
        discount = discount,
        minimumAmount = minimumAmount,
    )
}

private fun CouponDataModel.BuyNGetNCoupon(): Coupon.BuyNGetNCoupon? {
    if (buyQuantity == null || getQuantity == null) {
        return null
    }

    return Coupon.BuyNGetNCoupon(
        id = id,
        code = code,
        description = description,
        expirationDate = LocalDate.parse(expirationDate),
        buyQuantity = buyQuantity,
        getQuantity = getQuantity,
    )
}

private fun CouponDataModel.FreeShippingCoupon(): Coupon.FreeShippingCoupon? {
    if (minimumAmount == null) {
        return null
    }

    return Coupon.FreeShippingCoupon(
        id = id,
        code = code,
        description = description,
        expirationDate = LocalDate.parse(expirationDate),
        minimumAmount = minimumAmount,
    )
}

private fun CouponDataModel.PercentageCoupon(): Coupon.PercentageCoupon? {
    if (discount == null || availableTime == null) {
        return null
    }

    return Coupon.PercentageCoupon(
        id = id,
        code = code,
        description = description,
        expirationDate = LocalDate.parse(expirationDate),
        discountRate = discount.toDouble() / 100,
        availableTime =
            AvailableTime(
                start = LocalTime.parse(availableTime.start),
                end = LocalTime.parse(availableTime.end),
            ),
    )
}
