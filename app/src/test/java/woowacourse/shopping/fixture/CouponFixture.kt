package woowacourse.shopping.fixture

import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.DiscountType
import java.time.LocalDate

object CouponFixture {
    val fixedDummyCoupon =
        Coupon(
            code = "test_fixed",
            description = "테스트 쿠폰",
            expirationDate = LocalDate.of(2025, 12, 31),
            buyQuantity = null,
            getQuantity = null,
            discount = 10,
            discountType = DiscountType.FixedAmount(10_000),
            minimumAmount = 50_000,
            availableTime = null,
        )

    val percentDummyCoupon =
        Coupon(
            code = "test_percent",
            description = "테스트 퍼센트 쿠폰",
            expirationDate = LocalDate.of(2025, 12, 31),
            buyQuantity = null,
            getQuantity = null,
            discount = 10,
            discountType = DiscountType.Percentage(30),
            minimumAmount = 50_000,
            availableTime = null,
        )

    val freeShippingDummyCoupon =
        Coupon(
            code = "test_free_shipping",
            description = "테스트 무료배송 쿠폰",
            expirationDate = LocalDate.of(2025, 12, 31),
            buyQuantity = null,
            getQuantity = null,
            discount = null,
            discountType = DiscountType.FreeShipping,
            minimumAmount = 100_000,
            availableTime = null,
        )

    val buyXgetYDummyCoupon =
        Coupon(
            code = "test_buyX_getY",
            description = "테스트 2+1 쿠폰",
            expirationDate = LocalDate.of(2025, 12, 31),
            buyQuantity = 2,
            getQuantity = 1,
            discount = null,
            discountType = DiscountType.BuyXGetY(2, 1),
            minimumAmount = null,
            availableTime = null,
        )

    val couponList: List<Coupon> =
        listOf(
            fixedDummyCoupon,
            percentDummyCoupon,
            freeShippingDummyCoupon,
            buyXgetYDummyCoupon,
        )
}
