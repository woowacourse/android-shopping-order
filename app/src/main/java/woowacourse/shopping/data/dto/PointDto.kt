package woowacourse.shopping.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PointResponse(
    @SerialName("availablePoint")
    val availablePoint: Int = 0,
)
