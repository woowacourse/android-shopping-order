package woowacourse.shopping.remote.mapper

import woowacourse.shopping.domain.model.coupons.BOGO
import woowacourse.shopping.domain.model.coupons.Coupon
import woowacourse.shopping.domain.model.coupons.FIXED5000
import woowacourse.shopping.domain.model.coupons.FREESHIPPING
import woowacourse.shopping.domain.model.coupons.MIRACLESALE
import woowacourse.shopping.remote.model.response.CouponResponse
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun CouponResponse.toDomain(): Coupon {
    return when (this.code) {
        "FIXED5000" ->
            FIXED5000(
                id = this.id,
                code = this.code,
                description = this.description,
                expirationDate = this.expirationDate.toDomain(),
                discount = this.discount ?: 0,
                minimumAmount = this.minimumAmount ?: 0,
                discountType = this.discountType,
            )

        "BOGO" ->
            BOGO(
                id = this.id,
                code = this.code,
                description = this.description,
                expirationDate = this.expirationDate.toDomain(),
                buyQuantity = this.buyQuantity ?: 0,
                getQuantity = this.getQuantity ?: 0,
                discountType = this.discountType,
            )

        "FREESHIPPING" ->
            FREESHIPPING(
                id = this.id,
                code = this.code,
                description = this.description,
                expirationDate = this.expirationDate.toDomain(),
                minimumAmount = this.minimumAmount ?: 0,
                discountType = this.discountType,
            )

        "MIRACLESALE" ->
            MIRACLESALE(
                id = this.id,
                code = this.code,
                description = this.description,
                expirationDate = this.expirationDate.toDomain(),
                discount = this.discount ?: 0,
                availableTime = this.availableTime?.toDomain() ?: MIRACLESALE.AvailableLocalTime(LocalTime.MAX, LocalTime.MIN),
                discountType = this.discountType,
            )

        else -> throw IllegalArgumentException()
    }
}

fun CouponResponse.AvailableTime.toDomain(): MIRACLESALE.AvailableLocalTime {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    return MIRACLESALE.AvailableLocalTime(
        start = LocalTime.parse(this.start, timeFormatter),
        end = LocalTime.parse(this.end, timeFormatter),
    )
}

fun String.toDomain(): LocalDate {
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    return LocalDate.parse(this, dateFormatter)
}
