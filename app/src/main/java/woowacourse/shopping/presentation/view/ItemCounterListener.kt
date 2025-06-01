package woowacourse.shopping.presentation.view

import woowacourse.shopping.presentation.model.ProductUiModel

interface ItemCounterListener {
    fun increaseQuantity(product: ProductUiModel)

    fun decreaseQuantity(product: ProductUiModel)
}
