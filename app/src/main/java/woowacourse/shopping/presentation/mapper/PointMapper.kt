package woowacourse.shopping.presentation.mapper

import woowacourse.shopping.presentation.model.PointModel
import woowacouse.shopping.model.point.Point

fun Point.toUIModel(): PointModel = PointModel(value)

fun PointModel.toModel(): Point = Point(value)
