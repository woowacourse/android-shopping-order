package woowacourse.shopping.data.network.response.coupons

import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.coupon.MiracleSaleCoupon
import java.time.LocalTime

@Serializable
data class AvailableTime(
    val end: String,
    val start: String,
) {
    fun toDomain(): MiracleSaleCoupon.AvailableTime {
        return MiracleSaleCoupon.AvailableTime(
            end = LocalTime.parse(end),
            start = LocalTime.parse(start),
        )
    }
}
