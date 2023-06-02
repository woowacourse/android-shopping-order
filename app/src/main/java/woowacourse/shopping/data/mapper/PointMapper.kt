package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.PointEntity
import woowacourse.shopping.data.model.SavingPointEntity
import woowacouse.shopping.model.point.Point

fun PointEntity.toModel(): Point = Point(point)

fun SavingPointEntity.toModel(): Point = Point(savingPoint)
