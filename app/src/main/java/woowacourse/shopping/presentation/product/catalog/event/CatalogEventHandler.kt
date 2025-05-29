package woowacourse.shopping.presentation.product.catalog.event

import woowacourse.shopping.presentation.product.catalog.ProductUiModel

interface CatalogEventHandler {
    fun onProductClick(product: ProductUiModel)

    fun onLoadButtonClick()

    fun onOpenProductQuantitySelector(product: ProductUiModel)
}
