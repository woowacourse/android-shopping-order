package woowacourse.shopping.fixture

import woowacourse.shopping.domain.model.coupon.AvailableTime
import woowacourse.shopping.domain.model.coupon.BuyXGetYCoupon
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.FixedDiscountCoupon
import woowacourse.shopping.domain.model.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.model.coupon.PercentageDiscountCoupon
import java.time.LocalDate
import java.time.LocalTime

val couponsFixture: List<Coupon> =
    listOf(
        FixedDiscountCoupon(
            1,
            "FIXED5000",
            "5,000원 할인 쿠폰",
            LocalDate.now().plusDays(1),
            5000,
            1000,
        ),
        BuyXGetYCoupon(
            2,
            "BOGO",
            "2개 구매 시 1개 무료 쿠폰",
            LocalDate.now().plusDays(1),
            2,
            1,
        ),
        FreeShippingCoupon(
            3,
            "FREESHIPPING",
            "5만원 이상 구매 시 무료 배송 쿠폰",
            LocalDate.now().plusDays(1),
            50000,
        ),
        PercentageDiscountCoupon(
            4,
            "MIRACLESALE",
            "미라클모닝 30% 할인 쿠폰",
            LocalDate.now().plusDays(1),
            30,
            AvailableTime(LocalTime.of(0, 0), LocalTime.of(23, 59)),
        ),
    )
