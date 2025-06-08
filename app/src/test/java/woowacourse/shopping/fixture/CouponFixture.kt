package woowacourse.shopping.fixture

import woowacourse.shopping.data.model.Coupon
import woowacourse.shopping.data.model.CouponType

object CouponFixture {
    fun fixed5000Coupon(
        id: Long = 1L,
        expirationDate: String = "2025-10-11",
        discount: Long = 5000,
        minimumAmount: Long = 10000,
    ) = Coupon(
        id = id,
        description = "5,000원 할인 쿠폰",
        expirationDate = expirationDate,
        code = CouponType.FIXED5000.name,
        discount = discount,
        minimumAmount = minimumAmount,
        discountType = "fixed",
        buyQuantity = null,
        getQuantity = null,
        availableTime = null,
    )

    fun freeShippingCoupon(
        id: Long = 2L,
        expirationDate: String = "2025-08-31",
        minimumAmount: Long = 50000,
    ) = Coupon(
        id = id,
        description = "무료배송 쿠폰",
        expirationDate = expirationDate,
        code = CouponType.FREESHIPPING.name,
        discount = null,
        minimumAmount = minimumAmount,
        discountType = "freeShipping",
        buyQuantity = null,
        getQuantity = null,
        availableTime = null,
    )

    fun miracleSaleCoupon(
        id: Long = 3L,
        expirationDate: String = "2025-07-31",
        discountPercent: Long = 30,
        availableStart: String = "04:00:00",
        availableEnd: String = "07:00:00",
    ) = Coupon(
        id = id,
        description = "미라클모닝 $discountPercent% 할인 쿠폰",
        expirationDate = expirationDate,
        code = CouponType.MIRACLESALE.name,
        discount = discountPercent,
        minimumAmount = null,
        discountType = "percentage",
        buyQuantity = null,
        getQuantity = null,
        availableTime =
            Coupon.AvailableTime(
                start = availableStart,
                end = availableEnd,
            ),
    )

    fun bogoCoupon(
        id: Long = 4L,
        expirationDate: String = "2025-06-30",
        buyQuantity: Long = 2,
        getQuantity: Long = 1,
    ) = Coupon(
        id = id,
        description = "2개 구매 시 1개 무료 쿠폰",
        expirationDate = expirationDate,
        code = CouponType.BOGO.name,
        discount = null,
        minimumAmount = null,
        discountType = "buyXgetY",
        buyQuantity = buyQuantity,
        getQuantity = getQuantity,
        availableTime = null,
    )

    fun defaultCoupon(
        id: Long = 5L,
        expirationDate: String = "2025-10-11",
    ) = Coupon(
        id = id,
        description = "기본 쿠폰",
        expirationDate = expirationDate,
        code = CouponType.DEFAULT.name,
        discount = null,
        minimumAmount = null,
        discountType = "",
        buyQuantity = 0L,
        getQuantity = 0L,
        availableTime = null,
    )
}
