package woowacourse.shopping.data.remote.mapper

import woowacourse.shopping.data.remote.dto.response.coupon.AvailableTimeResponse
import woowacourse.shopping.data.remote.dto.response.coupon.CouponResponse
import woowacourse.shopping.model.coupon.AvailableTime
import woowacourse.shopping.model.coupon.BuyXGetYCoupon
import woowacourse.shopping.model.coupon.Coupon
import woowacourse.shopping.model.coupon.FixedDiscountCoupon
import woowacourse.shopping.model.coupon.FreeShippingCoupon
import woowacourse.shopping.model.coupon.PercentageDiscountCoupon
import java.time.LocalDate
import java.time.LocalTime

fun CouponResponse.toDomain(): Coupon {
    val parseExpirationDate = LocalDate.parse(expirationDate)

    return when (discountType) {
        "fixed" ->
            FixedDiscountCoupon(
                id = id,
                code = code,
                description = description,
                expirationDate = parseExpirationDate,
                discount = requireNotNull(discount),
                minimumAmount = requireNotNull(minimumAmount),
            )

        "percentage" ->
            PercentageDiscountCoupon(
                id = id,
                code = code,
                description = description,
                expirationDate = parseExpirationDate,
                discountRate = requireNotNull(discount),
                availableTime = requireNotNull(availableTime).toDomain(),
            )

        "buyXgetY" ->
            BuyXGetYCoupon(
                id = id,
                code = code,
                description = description,
                expirationDate = parseExpirationDate,
                buyQuantity = requireNotNull(buyQuantity),
                getQuantity = requireNotNull(getQuantity),
            )

        "freeShipping" ->
            FreeShippingCoupon(
                id = id,
                code = code,
                description = description,
                expirationDate = parseExpirationDate,
                minimumAmount = requireNotNull(minimumAmount),
            )

        else -> throw IllegalArgumentException("올바르지 않은 쿠폰입니다.")
    }
}

private fun AvailableTimeResponse.toDomain(): AvailableTime =
    AvailableTime(
        start = LocalTime.parse(start),
        end = LocalTime.parse(end),
    )
