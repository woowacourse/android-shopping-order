package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.remote.AvailableTimeDto
import woowacourse.shopping.data.model.remote.CouponDto
import woowacourse.shopping.data.model.remote.DiscountTypeDto
import woowacourse.shopping.domain.model.AvailableTime
import woowacourse.shopping.domain.model.coupon.*
import woowacourse.shopping.domain.model.coupon.FixedAmountDiscountPolicy.Companion.DELIVERY_FEE_DISCOUNT
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

fun List<CouponDto>.toDomain(): List<Coupon> {
    return map { it.toDomain() }
}

fun CouponDto.toDomain(): Coupon {
    val localDate = LocalDate.parse(this.expirationDate, DateTimeFormatter.ISO_DATE)
    val date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

    return Coupon(
        id = this.id,
        description = this.description,
        expirationDate = date,
        discountPolicy = this.discountType.toDiscountPolicy(this),
        couponCondition = this.discountType.toCouponCondition(this),
    )
}

fun DiscountTypeDto.toDiscountPolicy(couponDto: CouponDto): DiscountPolicy {
    return when (this) {
        DiscountTypeDto.FIXED -> FixedAmountDiscountPolicy(discount = couponDto.discount)
        DiscountTypeDto.BUY_X_GET_Y ->
            BogoDiscountPolicy(
                buyQuantity = couponDto.buyQuantity,
                getQuantity = couponDto.getQuantity,
            )

        DiscountTypeDto.FREE_SHIPPING -> FixedAmountDiscountPolicy(discount = DELIVERY_FEE_DISCOUNT)
        DiscountTypeDto.PERCENTAGE -> PercentageDiscountPolicy(couponDto.discount)
    }
}

fun DiscountTypeDto.toCouponCondition(couponDto: CouponDto): CouponCondition {
    val localDate = LocalDate.parse(couponDto.expirationDate, DateTimeFormatter.ISO_DATE)
    val date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

    return when (this) {
        DiscountTypeDto.FIXED ->
            MinimumPurchaseAmountCondition(
                expirationDate = date,
                minimumAmount = couponDto.minimumAmount,
            )

        DiscountTypeDto.BUY_X_GET_Y ->
            BogoCondition(
                expirationDate = date,
                buyQuantity = couponDto.buyQuantity,
            )

        DiscountTypeDto.FREE_SHIPPING ->
            MinimumPurchaseAmountCondition(
                expirationDate = date,
                minimumAmount = couponDto.minimumAmount,
            )

        DiscountTypeDto.PERCENTAGE ->
            TimeBasedCondition(
                expirationDate = date,
                availableTime = couponDto.availableTime?.toDomain(),
            )
    }
}

fun AvailableTimeDto.toDomain(): AvailableTime {
    return AvailableTime(
        start = LocalTime.parse(this.start),
        end = LocalTime.parse(this.end),
    )
}
