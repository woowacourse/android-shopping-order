package woowacourse.shopping.data.remote.dto.mapper

import woowacourse.shopping.data.remote.dto.response.AvailableTime

fun AvailableTime.toDomain(): woowacourse.shopping.domain.coupon.AvailableTime {
    return woowacourse.shopping.domain.coupon.AvailableTime(
        start = this.start,
        end = this.end,
    )
}
