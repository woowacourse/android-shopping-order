package woowacourse.shopping.data.model.order

import kotlinx.serialization.Serializable

@Serializable
data class PointDto(
    val currentPoint: Int,
    val toBeExpiredPoint: Int
)
