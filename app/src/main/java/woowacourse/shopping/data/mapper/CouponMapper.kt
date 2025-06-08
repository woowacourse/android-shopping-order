package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.response.CouponsResponse
import woowacourse.shopping.domain.model.CouponDetailInfo
import woowacourse.shopping.domain.model.CouponDiscountType
import java.time.LocalTime

fun CouponsResponse.CouponResponseItem.toDomain(): CouponDetailInfo =
    when (CouponDiscountType.from(id)) {
        CouponDiscountType.FIXED_DISCOUNT ->
            CouponDetailInfo.FixedDiscount(
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
            CouponDetailInfo.BuyXGetYFree(
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
            CouponDetailInfo.FreeShippingOver(
                id = id,
                code = code,
                description = description,
                expirationDate = expirationDate,
                minimumOrderAmount =
                    minimumAmount
                        ?: throw IllegalArgumentException("minimumOrderAmount is required"),
            )

        CouponDiscountType.PERCENT_DISCOUNT ->
            CouponDetailInfo.PercentDiscount(
                id = id,
                code = code,
                description = description,
                expirationDate = expirationDate,
                discount = discount ?: throw IllegalArgumentException("discount is required"),
                availableTime =
                    CouponDetailInfo.AvailableTime(
                        LocalTime.parse(availableTime?.start),
                        LocalTime.parse(availableTime?.end),
                    ),
            )
    }
