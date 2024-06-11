package woowacourse.shopping.data.order

import woowacourse.shopping.data.order.model.DiscountType
import woowacourse.shopping.domain.entity.coupon.BOGOCoupon
import woowacourse.shopping.domain.entity.coupon.Coupon
import woowacourse.shopping.domain.entity.coupon.Coupons
import woowacourse.shopping.domain.entity.coupon.FixedCoupon
import woowacourse.shopping.domain.entity.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.entity.coupon.PercentageCoupon
import woowacourse.shopping.remote.dto.response.CouponResponse
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

fun List<CouponResponse>.toDomain(): Coupons {
    return mapNotNull { it.toDomain() }.let(::Coupons)
}

fun CouponResponse.toDomain(): Coupon? {
    val discountType = DiscountType.from(discountType) ?: return null
    return when (discountType) {
        DiscountType.FIXED -> toFixedCoupon()
        DiscountType.BUY_X_GET_Y -> toBOGOCoupon()
        DiscountType.FREE_SHIPPING -> toFreeShippingCoupon()
        DiscountType.PERCENTAGE -> toPercentageCoupon()
    }
}

private fun CouponResponse.toFixedCoupon(): FixedCoupon {
    return FixedCoupon(
        id = id,
        code = code,
        description = description,
        expirationDate = expirationDate.toLocalDateTime(),
        discount = discount,
        discountableMinPrice = discountableMinPrice,
    )
}

private fun CouponResponse.toBOGOCoupon(): BOGOCoupon {
    return BOGOCoupon(
        id = id,
        code = code,
        description = description,
        expirationDate = expirationDate.toLocalDateTime(),
        buyCount = buyQuantity,
        freeCount = getQuantity,
    )
}

private fun CouponResponse.toFreeShippingCoupon(): FreeShippingCoupon {
    return FreeShippingCoupon(
        id = id,
        code = code,
        description = description,
        expirationDate = expirationDate.toLocalDateTime(),
        discountableMinPrice = discountableMinPrice,
    )
}

private fun CouponResponse.toPercentageCoupon(): PercentageCoupon {
    return PercentageCoupon(
        id = id,
        code = code,
        description = description,
        expirationDate = expirationDate.toLocalDateTime(),
        discountRate = discount.toFloat() / 100,
        availableStartTime = LocalTime.parse(availableTime.startTime, timeFormatter),
        availableEndTime = LocalTime.parse(availableTime.endTime, timeFormatter),
    )
}

private fun String.toLocalDateTime(): LocalDateTime {
    return LocalDate.parse(this, dateFormatter)
        .atTime(LocalTime.MAX)
}

private fun String.toLocalTime(): LocalTime {
    return LocalTime.parse(this, timeFormatter)
}
