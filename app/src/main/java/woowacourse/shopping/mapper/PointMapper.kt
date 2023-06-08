package woowacourse.shopping.mapper

import com.example.domain.model.Point
import woowacourse.shopping.model.PointUiModel

fun Point.toPresentation(): PointUiModel = PointUiModel(currentPoint, toBeExpiredPoint)
