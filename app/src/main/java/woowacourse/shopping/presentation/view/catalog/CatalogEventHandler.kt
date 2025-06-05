package woowacourse.shopping.presentation.view.catalog

import woowacourse.shopping.presentation.model.ProductUiModel

interface CatalogEventHandler {
    fun increaseQuantity(product: ProductUiModel)

    fun onProductClicked(product: ProductUiModel)

    fun onLoadMoreClicked()
}
