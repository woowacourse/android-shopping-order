package woowacourse.shopping.model.mapper

import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.model.UiPrice

fun UiPrice.toDomain(): Price =
    Price(value)

fun Price.toUi(): UiPrice =
    UiPrice(value)
