package woowacourse.shopping.ui.products

import woowacourse.shopping.model.ProductWithQuantity

fun ProductWithQuantity.toUiModel() =
    ProductWithQuantityUiModel(
        product,
        quantity.value,
    )
