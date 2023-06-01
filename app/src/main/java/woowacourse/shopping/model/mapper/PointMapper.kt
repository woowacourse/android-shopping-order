package woowacourse.shopping.model.mapper

import woowacourse.shopping.domain.model.discount.Discountable
import woowacourse.shopping.domain.model.discount.Point
import woowacourse.shopping.model.PointModel

fun PointModel.toDomain(): Point = Point(
    value = value,
)

/**
 * Discountable 여러개인 경우를 가정하여 mapper method 네이밍을 구체화함
 **/
fun Discountable.toPointModelUi(): PointModel = PointModel(
    value = value,
)
