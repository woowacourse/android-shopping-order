package woowacourse.shopping.data.source.remote.dto.coupon.mapper

import woowacourse.shopping.data.source.remote.dto.coupon.CouponResponse
import woowacourse.shopping.domain.model.AvailableTime
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.Money
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun CouponResponse.toDomain(): Coupon =
    when (this) {
        is CouponResponse.Fixed ->
            Coupon.Fixed(
                id = this.id,
                description = this.description,
                expirationDate = parseDate(this.expirationDate),
                discount = this.discount,
                minimumAmount = Money(amount = this.minimumAmount),
            )
        is CouponResponse.BuyXGetY ->
            Coupon.BuyXGetY(
                id = this.id,
                description = this.description,
                expirationDate = parseDate(this.expirationDate),
                buyQuantity = this.buyQuantity,
                getQuantity = this.getQuantity,
            )
        is CouponResponse.FreeShipping ->
            Coupon.FreeShipping(
                id = this.id,
                description = this.description,
                expirationDate = parseDate(this.expirationDate),
                minimumAmount = Money(amount = this.minimumAmount),
            )
        is CouponResponse.Percentage ->
            Coupon.Percentage(
                id = this.id,
                description = this.description,
                expirationDate = parseDate(this.expirationDate),
                discount = this.discount,
                availableTime =
                    AvailableTime(
                        start = parseTime(this.availableTime.start),
                        end = parseTime(this.availableTime.end),
                    ),
            )
    }

private fun parseDate(date: String): LocalDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE)

private fun parseTime(time: String): LocalTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm:ss"))
