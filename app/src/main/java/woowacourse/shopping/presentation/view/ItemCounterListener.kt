package woowacourse.shopping.presentation.view

import woowacourse.shopping.presentation.model.ProductUiModel

interface ItemCounterListener {
    fun increase(product: ProductUiModel)

    fun decrease(product: ProductUiModel)
}
