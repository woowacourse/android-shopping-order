package woowacourse.shopping.data.coupon.remote.dto

import woowacourse.shopping.domain.model.coupon.AvailableTime

data class AvailableTimeDto(
    val start: String,
    val end: String,
) {
    companion object {
        fun AvailableTimeDto.toDomain() =
            AvailableTime(
                start = start,
                end = end,
            )
    }
}
