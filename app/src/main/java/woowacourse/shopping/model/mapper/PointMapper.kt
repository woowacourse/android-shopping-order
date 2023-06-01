package woowacourse.shopping.model.mapper

import woowacourse.shopping.domain.model.Point
import woowacourse.shopping.model.UiPoint

fun UiPoint.toDomain(): Point = Point(
    value = value,
)

fun Point.toUi(): UiPoint = UiPoint(
    value = value,
)
