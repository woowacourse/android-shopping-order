package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.remote.AvailableTimeDto
import woowacourse.shopping.data.model.remote.CouponDto
import woowacourse.shopping.domain.model.AvailableTime
import woowacourse.shopping.domain.model.coupon.Bogo
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.CouponState
import woowacourse.shopping.domain.model.coupon.Fixed5000
import woowacourse.shopping.domain.model.coupon.FreeShipping
import woowacourse.shopping.domain.model.coupon.MiracleSale
import woowacourse.shopping.domain.model.coupon.UnknownCoupon
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

fun List<CouponDto>.toDomainModel(): List<CouponState> {
    return map { it.toDomainModel() }
}

fun CouponDto.toDomainModel(): CouponState {
    return when (this.code) {
        "FIXED5000" -> Fixed5000(this.toCoupon())
        "BOGO" -> Bogo(this.toCoupon())
        "FREESHIPPING" -> FreeShipping(this.toCoupon())
        "MIRACLESALE" -> MiracleSale(this.toCoupon())
        else -> UnknownCoupon(this.toCoupon())
    }
}

private fun CouponDto.toCoupon(): Coupon {
    val localDate = LocalDate.parse(this.expirationDate, DateTimeFormatter.ISO_DATE)
    val date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

    return Coupon(
        id = this.id,
        description = this.description,
        expirationDate = date,
        discount = this.discount,
        minimumAmount = this.minimumAmount,
        buyQuantity = this.buyQuantity,
        getQuantity = this.getQuantity,
        availableTime = this.availableTime?.toDomain(),
        discountType = this.discountType,
    )
}

fun AvailableTimeDto.toDomain(): AvailableTime {
    return AvailableTime(
        start = LocalTime.parse(this.start),
        end = LocalTime.parse(this.end),
    )
}
