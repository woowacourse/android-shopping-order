package woowacourse.shopping.ui.mapper

import woowacourse.shopping.domain.Price
import woowacourse.shopping.ui.model.PriceUiModel

fun PriceUiModel.toDomainModel(): Price =
    Price(value)

fun Price.toUiModel(): PriceUiModel =
    PriceUiModel(value)
