package woowacourse.shopping.data.common

import woowacourse.shopping.data.coupon.remote.dto.CouponResponse
import woowacourse.shopping.domain.coupon.BuyXGetY
import woowacourse.shopping.domain.coupon.Fixed
import woowacourse.shopping.domain.coupon.FreeShipping
import woowacourse.shopping.domain.coupon.Percentage
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun CouponResponse.toBuyXGetY(): BuyXGetY {
    return BuyXGetY(
        id = id.toLong(),
        description = description,
        code = code,
        explanationDate = LocalDate.parse(expirationDate, DateTimeFormatter.ISO_LOCAL_DATE),
        buyQuantity = buyQuantity!!,
        getQuantity = getQuantity!!,
    )
}

fun CouponResponse.toFixed(): Fixed {
    return Fixed(
        id = id.toLong(),
        description = description,
        code = code,
        explanationDate = LocalDate.parse(expirationDate, DateTimeFormatter.ISO_LOCAL_DATE),
        discount = discount!!,
        minimumAmount = minimumAmount,
    )
}

fun CouponResponse.toPercentage(): Percentage {
    if (availableTime != null) {
        return Percentage(
            id = id.toLong(),
            description = description,
            code = code,
            explanationDate = LocalDate.parse(expirationDate, DateTimeFormatter.ISO_LOCAL_DATE),
            discount = discount!!,
            availableStartTime =
                LocalTime.parse(
                    availableTime.start,
                    DateTimeFormatter.ISO_LOCAL_TIME,
                ),
            availableEndTime = LocalTime.parse(availableTime.end, DateTimeFormatter.ISO_LOCAL_TIME),
        )
    }
    return Percentage(
        id = id.toLong(),
        description = description,
        code = code,
        explanationDate = LocalDate.parse(expirationDate, DateTimeFormatter.ISO_LOCAL_DATE),
        discount = discount!!,
    )
}

fun CouponResponse.toFreeShipping(): FreeShipping {
    return FreeShipping(
        id = id.toLong(),
        description = description,
        code = code,
        explanationDate = LocalDate.parse(expirationDate, DateTimeFormatter.ISO_LOCAL_DATE),
        minimumAmount = minimumAmount,
    )
}
