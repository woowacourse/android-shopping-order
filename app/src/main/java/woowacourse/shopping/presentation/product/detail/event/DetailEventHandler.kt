package woowacourse.shopping.presentation.product.detail.event

import woowacourse.shopping.presentation.product.catalog.ProductUiModel

interface DetailEventHandler {
    fun onAddCartItem()

    fun onProductClick(product: ProductUiModel)
}
