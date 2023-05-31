package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.DataPoint
import woowacourse.shopping.domain.Point

fun DataPoint.toDomain(): Point =
    Point(value)

fun Point.toData(): DataPoint =
    DataPoint(value)
