package woowacourse.shopping.remote.mapper

import woowacourse.shopping.domain.model.AvailableLocalTime
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.remote.model.response.CouponResponse
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun CouponResponse.toDomain(): Coupon {
    return Coupon(
        id = this.id,
        code = this.code,
        description = this.description,
        expirationDate = this.expirationDate.toDomain(),
        discount = this.discount,
        minimumAmount = this.minimumAmount,
        buyQuantity = this.buyQuantity,
        getQuantity = this.getQuantity,
        availableTime = this.availableTime?.toDomain(),
        discountType = this.discountType,
    )
}

fun CouponResponse.AvailableTime.toDomain(): AvailableLocalTime {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    return AvailableLocalTime(
        start = LocalTime.parse(this.start, timeFormatter),
        end = LocalTime.parse(this.end, timeFormatter),
    )
}

fun String.toDomain(): LocalDate {
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    return LocalDate.parse(this, dateFormatter)
}
