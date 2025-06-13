package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.response.CouponsResponse
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.CouponDiscountType
import java.time.LocalTime

fun CouponsResponse.CouponResponseItem.toDomain(): Coupon =
    when (CouponDiscountType.from(discountType)) {
        CouponDiscountType.FIXED_DISCOUNT ->
            Coupon.FixedDiscount(
                id = id,
                code = code,
                description = description,
                expirationDate = expirationDate,
                discount = discount ?: throw IllegalArgumentException("discount is required"),
                minimumOrderAmount =
                    minimumAmount
                        ?: throw IllegalArgumentException("minimumOrderAmount is required"),
            )

        CouponDiscountType.BUY_X_GET_Y_FREE ->
            Coupon.BuyXGetYFree(
                id = id,
                code = code,
                description = description,
                expirationDate = expirationDate,
                buyQuantity =
                    buyQuantity
                        ?: throw IllegalArgumentException("buyQuantity is required"),
                getQuantity =
                    getQuantity
                        ?: throw IllegalArgumentException("getQuantity is required"),
            )

        CouponDiscountType.FREE_SHIPPING_OVER ->
            Coupon.FreeShippingOver(
                id = id,
                code = code,
                description = description,
                expirationDate = expirationDate,
                minimumOrderAmount =
                    minimumAmount
                        ?: throw IllegalArgumentException("minimumOrderAmount is required"),
            )

        CouponDiscountType.PERCENT_DISCOUNT ->
            Coupon.PercentDiscount(
                id = id,
                code = code,
                description = description,
                expirationDate = expirationDate,
                discount = discount ?: throw IllegalArgumentException("discount is required"),
                availableTime =
                    Coupon.AvailableTime(
                        LocalTime.parse(availableTime?.start),
                        LocalTime.parse(availableTime?.end),
                    ),
            )
    }
