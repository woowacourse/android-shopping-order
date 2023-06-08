package woowacourse.shopping.ui.mapper

import woowacourse.shopping.domain.Point
import woowacourse.shopping.ui.model.UiPoint

fun UiPoint.toDomain(): Point =
    Point(value)

fun Point.toUi(): UiPoint =
    UiPoint(value)
