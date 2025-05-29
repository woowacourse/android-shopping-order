package woowacourse.shopping.domain.repository

import woowacourse.shopping.presentation.product.catalog.ProductUiModel

interface ViewedItemRepository {
    fun insertViewedItem(
        product: ProductUiModel,
        onComplete: () -> Unit,
    )

    fun getViewedItems(callback: (List<ProductUiModel>) -> Unit)

    fun getLastViewedItem(callback: (ProductUiModel?) -> Unit)
}
