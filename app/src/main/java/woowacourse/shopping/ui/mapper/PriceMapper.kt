package woowacourse.shopping.ui.mapper

import woowacourse.shopping.domain.Price
import woowacourse.shopping.ui.model.PriceUiModel

fun PriceUiModel.toProductDomainModel(): Price =
    Price(value)

fun Price.toPriceUiModel(): PriceUiModel =
    PriceUiModel(value)
