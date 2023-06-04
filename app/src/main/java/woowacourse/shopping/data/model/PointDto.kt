package woowacourse.shopping.data.model

import com.example.domain.model.Point

data class PointDto(
    val currentPoint: Int,
    val toBeExpiredPoint: Int
)

fun PointDto.toDomain() = Point(
    currentPoint, toBeExpiredPoint
)
