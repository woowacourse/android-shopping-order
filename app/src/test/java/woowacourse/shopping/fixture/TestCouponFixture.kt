package woowacourse.shopping.fixture

import java.time.LocalDate
import java.time.LocalTime
import woowacourse.shopping.domain.coupon.BuyXGetYCoupon
import woowacourse.shopping.domain.coupon.FixedDiscountCoupon
import woowacourse.shopping.domain.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.coupon.PercentageCoupon

object TestCouponFixture {

    val EXPIRATION_DATE: LocalDate = LocalDate.of(2026, 12, 31)

    val FIXED_1000 =
        FixedDiscountCoupon(expirationDate = EXPIRATION_DATE, minimumPrice = 10_000, discountPrice = 1_000)
    val FIXED_2000 =
        FixedDiscountCoupon(expirationDate = EXPIRATION_DATE, minimumPrice = 15_000, discountPrice = 2_000)
    val FIXED_3000 =
        FixedDiscountCoupon(expirationDate = EXPIRATION_DATE, minimumPrice = 20_000, discountPrice = 3_000)
    val FIXED_5000 =
        FixedDiscountCoupon(expirationDate = EXPIRATION_DATE, minimumPrice = 30_000, discountPrice = 5_000)
    val FIXED_10000 =
        FixedDiscountCoupon(expirationDate = EXPIRATION_DATE, minimumPrice = 50_000, discountPrice = 10_000)

    val FREE_SHIP_0 = FreeShippingCoupon(expirationDate = EXPIRATION_DATE, minimumPrice = 0)
    val FREE_SHIP_10000 = FreeShippingCoupon(expirationDate = EXPIRATION_DATE, minimumPrice = 10_000)
    val FREE_SHIP_20000 = FreeShippingCoupon(expirationDate = EXPIRATION_DATE, minimumPrice = 20_000)
    val FREE_SHIP_30000 = FreeShippingCoupon(expirationDate = EXPIRATION_DATE, minimumPrice = 30_000)
    val FREE_SHIP_50000 = FreeShippingCoupon(expirationDate = EXPIRATION_DATE, minimumPrice = 50_000)

    val BOGO = BuyXGetYCoupon(
        expirationDate = EXPIRATION_DATE,
        buyQuantity = 2,
        getQuantity = 1,
    )
    val MIRACLE_MORNING = PercentageCoupon(
        startTime = LocalTime.of(4, 0),
        endTime = LocalTime.of(7, 0), expirationDate = EXPIRATION_DATE,
    )

    val ALL = listOf(
        FIXED_1000, FIXED_2000, FIXED_3000, FIXED_5000, FIXED_10000,
        FREE_SHIP_0, FREE_SHIP_10000, FREE_SHIP_20000, FREE_SHIP_30000, FREE_SHIP_50000,
        BOGO,
        MIRACLE_MORNING,
    )
}
