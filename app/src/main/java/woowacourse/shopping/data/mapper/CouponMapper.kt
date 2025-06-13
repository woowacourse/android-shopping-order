package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.response.CouponsResponse
import woowacourse.shopping.domain.model.Coupon
import java.time.LocalTime

fun CouponsResponse.CouponResponseItem.toDomain(): Coupon =
    when (this.discountType) {
        "fixed" ->
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

        "buyXgetY" ->
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

        "freeShipping" ->
            Coupon.FreeShippingOver(
                id = id,
                code = code,
                description = description,
                expirationDate = expirationDate,
                minimumOrderAmount =
                    minimumAmount
                        ?: throw IllegalArgumentException("minimumOrderAmount is required"),
            )

        "percentage" ->
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

        else -> throw IllegalArgumentException()
    }
