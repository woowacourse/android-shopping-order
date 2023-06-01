package woowacourse.shopping.model.mapper

import woowacourse.shopping.domain.model.Point
import woowacourse.shopping.model.PointModel

fun PointModel.toDomain(): Point = Point(
    value = value,
)

fun Point.toUi(): PointModel = PointModel(
    value = value,
)
