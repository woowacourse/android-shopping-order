package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.remote.model.dto.coupon.BuyXGetYCouponDto
import woowacourse.shopping.data.remote.model.dto.coupon.CouponDto
import woowacourse.shopping.data.remote.model.dto.coupon.FixedDiscountCouponDto
import woowacourse.shopping.data.remote.model.dto.coupon.FreeShippingCouponDto
import woowacourse.shopping.data.remote.model.dto.coupon.PercentageDiscountCouponDto
import woowacourse.shopping.domain.model.coupon.AvailableTime
import woowacourse.shopping.domain.model.coupon.BuyXGetYCoupon
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.FixedDiscountCoupon
import woowacourse.shopping.domain.model.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.model.coupon.PercentageDiscountCoupon
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private const val DATE_PARSE_PATTERN = "yyyy-MM-dd"
private const val TIME_PARSE_PATTERN = "HH:mm:ss"

fun CouponDto.toDomainModel(): Coupon {
    return when (this) {
        is FixedDiscountCouponDto -> {
            FixedDiscountCoupon(
                id = id,
                code = code,
                description = description,
                expirationDate = convertStringToLocalDate(expirationDate),
                discountType = discountType,
                discount = discount,
                minimumAmount = minimumAmount,
            )
        }

        is BuyXGetYCouponDto -> {
            BuyXGetYCoupon(
                id = id,
                code = code,
                description = description,
                expirationDate = convertStringToLocalDate(expirationDate),
                discountType = discountType,
                buyQuantity = buyQuantity,
                getQuantity = getQuantity,
            )
        }

        is FreeShippingCouponDto -> {
            FreeShippingCoupon(
                id = id,
                code = code,
                description = description,
                expirationDate = convertStringToLocalDate(expirationDate),
                discountType = discountType,
                minimumAmount = minimumAmount,
            )
        }

        is PercentageDiscountCouponDto -> {
            PercentageDiscountCoupon(
                id = id,
                code = code,
                description = description,
                expirationDate = convertStringToLocalDate(expirationDate),
                discountType = discountType,
                discount = discount,
                availableTime = AvailableTime(
                    start = convertStringToLocalTime(availableTime.start),
                    end = convertStringToLocalTime(availableTime.end),
                ),
            )
        }
    }
}

fun convertStringToLocalDate(date: String): LocalDate {
    return LocalDate.parse(date, DateTimeFormatter.ofPattern(DATE_PARSE_PATTERN))
}

fun convertStringToLocalTime(time: String): LocalTime {
    return LocalTime.parse(time, DateTimeFormatter.ofPattern(TIME_PARSE_PATTERN))
}
