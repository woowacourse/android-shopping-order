package woowacourse.shopping.ui.products

import woowacourse.shopping.domain.model.product.ProductWithQuantity

fun ProductWithQuantity.toUiModel() =
    ProductWithQuantityUiModel(
        product,
        quantity.value,
    )
