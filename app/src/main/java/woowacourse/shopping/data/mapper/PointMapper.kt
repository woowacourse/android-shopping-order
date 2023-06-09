package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.dto.response.PointResponse
import woowacourse.shopping.data.model.dto.response.SavingPointResponse
import woowacouse.shopping.model.point.Point

fun PointResponse.toModel(): Point = Point(point)

fun SavingPointResponse.toModel(): Point = Point(savingPoint)
