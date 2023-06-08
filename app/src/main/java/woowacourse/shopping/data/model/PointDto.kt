package woowacourse.shopping.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PointDto(
    val currentPoint: Int,
    val toBeExpiredPoint: Int
)
