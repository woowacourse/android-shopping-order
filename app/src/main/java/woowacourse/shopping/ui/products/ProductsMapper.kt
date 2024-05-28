package woowacourse.shopping.ui.products

import woowacourse.shopping.model.ProductWithQuantity

fun ProductWithQuantity.toProductUiModel() =
    ProductWithQuantityUiModel(
        product,
        quantity.value,
    )
