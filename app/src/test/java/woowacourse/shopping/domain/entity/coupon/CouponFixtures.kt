package woowacourse.shopping.domain.entity.coupon

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

private val targetDateTime = LocalDateTime.of(2024, 5, 1, 0, 0)
private val targetDate = LocalDate.of(2024, 5, 1)

fun fakeCoupons(vararg coupons: Coupon): Coupons {
    return Coupons(
        coupons.toList(),
    )
}

fun fakePercentageCoupon(
    isExpired: Boolean = false,
    discountRate: Float = 0.3f,
    currentTime: Int = 5,
    availableStartTime: Int = 4,
    availableEndTime: Int = 7,
): PercentageCoupon {
    val targetDateTime = LocalDateTime.of(targetDate, LocalTime.of(currentTime, 0))
    val expirationDate =
        if (isExpired) {
            targetDateTime.minusDays(1)
        } else {
            targetDateTime.plusDays(1)
        }
    return PercentageCoupon(
        id = 1,
        code = "PERCENTAGE",
        description = "30% 할인 쿠폰",
        targetDateTime = targetDateTime,
        expirationDate = expirationDate,
        discountRate = discountRate,
        availableStartTime = LocalTime.of(availableStartTime, 0),
        availableEndTime = LocalTime.of(availableEndTime, 0),
    )
}

fun fakeFreeShippingCoupon(
    isExpired: Boolean = false,
    discountableMinPrice: Long = 50_000,
): FreeShippingCoupon {
    val targetDateTime = targetDateTime
    val expirationDate =
        if (isExpired) {
            targetDateTime.minusDays(1)
        } else {
            targetDateTime.plusDays(1)
        }
    return FreeShippingCoupon(
        id = 1,
        code = "FREE_SHIPPING",
        description = "무료 배송 쿠폰",
        targetDateTime = targetDateTime,
        expirationDate = expirationDate,
        discountableMinPrice = discountableMinPrice,
    )
}

fun fakeBuyXGetYCoupon(
    isExpired: Boolean = false,
    buyCount: Int = 2,
    freeCount: Int = 1,
): BuyXGetYCoupon {
    val targetDateTime = targetDateTime
    val expirationDate =
        if (isExpired) {
            targetDateTime.minusDays(1)
        } else {
            targetDateTime.plusDays(1)
        }
    return BuyXGetYCoupon(
        id = 1,
        code = "BUY_X_GET_Y",
        description = "BOGO 쿠폰",
        targetDateTime = targetDateTime,
        expirationDate = expirationDate,
        buyCount = buyCount,
        freeCount = freeCount,
    )
}

fun fakeFixedCoupon(
    discount: Long = 5_000,
    discountableMinPrice: Long = 100_000,
    isExpired: Boolean = false,
): FixedCoupon {
    val targetDateTime = targetDateTime
    val expirationDate =
        if (isExpired) {
            targetDateTime.minusDays(1)
        } else {
            targetDateTime.plusDays(1)
        }
    return FixedCoupon(
        id = 1,
        code = "FIXED",
        description = "5,000원 할인 쿠폰",
        targetDateTime = targetDateTime,
        expirationDate = expirationDate,
        discount = discount,
        discountableMinPrice = discountableMinPrice,
    )
}
