package woowacourse.shopping.data.model.coupon

import kotlinx.serialization.Serializable
import woowacourse.shopping.data.util.LocalTimeSerializer
import java.time.LocalTime
import woowacourse.shopping.domain.model.coupon.AvailableTime as DomainAvailableTime

@Serializable
data class AvailableTime(
    @Serializable(with = LocalTimeSerializer::class)
    val start: LocalTime,
    @Serializable(with = LocalTimeSerializer::class)
    val end: LocalTime,
)

fun AvailableTime.toDomain(): DomainAvailableTime = DomainAvailableTime(start, end)
