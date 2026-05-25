package woowacourse.shopping.mapper

import woowacourse.shopping.backend.retrofit.dto.coupon.CouponResponse
import woowacourse.shopping.model.coupon.BuyXGetYCoupon
import woowacourse.shopping.model.coupon.Coupon
import woowacourse.shopping.model.coupon.FixedDiscountCoupon
import woowacourse.shopping.model.coupon.FreeShippingCoupon
import woowacourse.shopping.model.coupon.PercentageDiscountCoupon

fun CouponResponse.toDomain(): Coupon =
    when (discountType) {
        "fixed" ->
            FixedDiscountCoupon(
                id = id,
                code = code,
                description = description,
                expirationDate = expirationDate.toLocalDate(),
                discount = requireNotNull(discount),
                minimumAmount = requireNotNull(minimumAmount),
            )

        "percentage" ->
            PercentageDiscountCoupon(
                id = id,
                code = code,
                description = description,
                expirationDate = expirationDate.toLocalDate(),
                discountPercentage = requireNotNull(discount),
                availableTime = requireNotNull(availableTime).toDomain(),
            )

        "buyXgetY" ->
            BuyXGetYCoupon(
                id = id,
                code = code,
                description = description,
                expirationDate = expirationDate.toLocalDate(),
                buyQuantity = requireNotNull(buyQuantity),
                getQuantity = requireNotNull(getQuantity),
            )

        "freeShipping" ->
            FreeShippingCoupon(
                id = id,
                code = code,
                description = description,
                expirationDate = expirationDate.toLocalDate(),
                minimumAmount = requireNotNull(minimumAmount),
            )

        else -> error("알 수 없는 discountType: $discountType")
    }
