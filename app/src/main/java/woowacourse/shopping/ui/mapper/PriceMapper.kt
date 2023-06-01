package woowacourse.shopping.ui.mapper

import woowacourse.shopping.domain.Price
import woowacourse.shopping.ui.model.PriceUiModel

fun PriceUiModel.toDomain(): Price =
    Price(value)

fun Price.toUi(): PriceUiModel =
    PriceUiModel(value)
