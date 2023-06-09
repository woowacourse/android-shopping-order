package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.dto.PointDto
import woowacourse.shopping.domain.model.Point

fun PointDto.toDomain(): Point =
    Point(
        availablePoint = availablePoint,
    )

fun Point.toDto(): PointDto =
    PointDto(
        availablePoint = availablePoint,
    )
