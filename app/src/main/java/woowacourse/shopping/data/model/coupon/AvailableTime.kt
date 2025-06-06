package woowacourse.shopping.data.model.coupon

import kotlinx.serialization.Serializable
import woowacourse.shopping.data.util.LocalTimeSerializer
import java.time.LocalTime

@Serializable
data class AvailableTime(
    @Serializable(with = LocalTimeSerializer::class)
    val start: LocalTime,
    @Serializable(with = LocalTimeSerializer::class)
    val end: LocalTime,
)
