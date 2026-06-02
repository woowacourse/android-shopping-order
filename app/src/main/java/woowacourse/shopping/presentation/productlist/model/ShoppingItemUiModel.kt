package woowacourse.shopping.presentation.productlist.model

import woowacourse.shopping.presentation.common.model.ProductUiModel

data class ShoppingItemUiModel(
    val product: ProductUiModel,
    val quantity: Int,
)
