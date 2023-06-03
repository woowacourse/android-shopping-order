package woowacourse.shopping.ui.mapper

import woowacourse.shopping.domain.OrderItem
import woowacourse.shopping.ui.model.UiOrderItem

fun UiOrderItem.toDomain(): OrderItem =
    OrderItem(
        count = count.toDomain(),
        product = product.toDomain()
    )

fun OrderItem.toUi(): UiOrderItem =
    UiOrderItem(
        count = count.toUi(),
        product = product.toUi()
    )
