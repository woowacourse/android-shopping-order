package woowacourse.shopping.ui.products

import woowacourse.shopping.domain.model.ProductWithQuantity
import woowacourse.shopping.ui.products.uimodel.ProductWithQuantityUiModel

fun ProductWithQuantity.toUiModel() =
    ProductWithQuantityUiModel(
        product,
        quantity.value,
    )
