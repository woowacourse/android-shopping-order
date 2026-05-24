package woowacourse.shopping.domain.model.coupon

import java.time.LocalDate
import java.time.LocalTime

object CouponInfos {
    val defaultCoupons: List<Coupon> =
        listOf(
            FixedAmountCoupon(
                code = "FIXED5000",
                name = "5,000원 할인 쿠폰",
                expirationDate = LocalDate.of(2026, 11, 30),
                discountAmount = 5_000,
                minimumOrderAmount = 100_000,
            ),
            NplusMFreeCoupon(
                code = "BOGO",
                name = "2개 구매 시 1개 무료 쿠폰",
                expirationDate = LocalDate.of(2026, 5, 30),
                purchaseQuantity = 2,
                freeQuantity = 1,
            ),
            FreeShippingCoupon(
                code = "FREESHIPPING",
                name = "5만원 이상 구매 시 무료 배송 쿠폰",
                expirationDate = LocalDate.of(2026, 8, 31),
                minimumOrderAmount = 50_000,
            ),
            TimeBasedPercentCoupon(
                code = "MIRACLESALE",
                name = "미라클모닝 30% 할인 쿠폰",
                expirationDate = LocalDate.of(2026, 7, 31),
                discountRate = 0.3,
                startTime = LocalTime.of(4, 0),
                endTime = LocalTime.of(7, 0),
            ),
        )
}
