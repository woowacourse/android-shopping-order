package woowacourse.shopping.data.dto.order

import com.google.gson.annotations.SerializedName

data class AvailableTime(
    @SerializedName("end")
    val end: String,
    @SerializedName("start")
    val start: String,
)

fun AvailableTime?.toDomain(): woowacourse.shopping.order.AvailableTime? {
    if (this == null) return null
    return woowacourse.shopping.order.AvailableTime(
        start = start,
        end = end,
    )
}
