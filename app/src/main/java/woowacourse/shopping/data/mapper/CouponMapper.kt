package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.remote.dto.CouponResponseDto
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.coupon.Coupons
import woowacourse.shopping.domain.coupon.TimeRange
import java.time.LocalDate
import java.time.LocalTime

fun List<CouponResponseDto>.toDomain(): Coupons =
    Coupons(
        coupons = map { it.toDomain() },
    )

fun CouponResponseDto.toDomain(): Coupon =
    when (this) {
        is CouponResponseDto.Fixed ->
            Coupon.FixedCoupon(
                id = id,
                code = code,
                description = description,
                expirationDate = LocalDate.parse(expirationDate),
                discountAmount = discount,
                minimumAmount = minimumAmount,
            )

        is CouponResponseDto.Percentage ->
            Coupon.PercentageCoupon(
                id = id,
                code = code,
                description = description,
                expirationDate = LocalDate.parse(expirationDate),
                discountPercentage = discount,
                availableTime =
                    TimeRange(
                        start = LocalTime.parse(availableTime.start),
                        end = LocalTime.parse(availableTime.end),
                    ),
            )

        is CouponResponseDto.BuyXGetY ->
            Coupon.BuyXGetYCoupon(
                id = id,
                code = code,
                description = description,
                expirationDate = LocalDate.parse(expirationDate),
                buyQuantity = buyQuantity,
                getQuantity = getQuantity,
            )

        is CouponResponseDto.FreeShipping ->
            Coupon.FreeShipping(
                id = id,
                code = code,
                description = description,
                expirationDate = LocalDate.parse(expirationDate),
                minimumAmount = minimumAmount,
            )
    }
