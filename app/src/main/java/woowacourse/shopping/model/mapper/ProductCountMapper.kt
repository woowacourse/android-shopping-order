package woowacourse.shopping.model.mapper

import woowacourse.shopping.domain.model.ProductCount
import woowacourse.shopping.model.ProductCountModel

fun ProductCountModel.toDomain(): ProductCount =
    ProductCount(value)

fun ProductCount.toUi(): ProductCountModel =
    ProductCountModel(value)
