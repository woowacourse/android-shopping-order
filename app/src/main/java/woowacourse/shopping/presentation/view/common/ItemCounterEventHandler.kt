package woowacourse.shopping.presentation.view.common

import woowacourse.shopping.presentation.model.ProductUiModel

interface ItemCounterEventHandler {
    fun increaseQuantity(product: ProductUiModel)

    fun decreaseQuantity(product: ProductUiModel)
}
