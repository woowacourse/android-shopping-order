package woowacourse.shopping.model.mapper

import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.model.PriceModel

fun PriceModel.toDomain(): Price =
    Price(value)

fun Price.toUi(): PriceModel =
    PriceModel(value)
