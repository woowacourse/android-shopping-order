package woowacourse.shopping.mapper

import woowacourse.shopping.backend.retrofit.dto.coupon.ExpirationDate
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

fun String.toExpirationDate(): ExpirationDate {
    val localDate = LocalDate.parse(this)
    return ExpirationDate(
        year = localDate.year,
        month = localDate.monthValue,
        day = localDate.dayOfMonth,
    )
}