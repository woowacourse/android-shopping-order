package woowacourse.shopping.data.repository

import woowacourse.shopping.product.catalog.ProductUiModel

interface ViewedItemRepository {
    fun insertViewedItem(
        product: ProductUiModel,
        onComplete: () -> Unit,
    )

    fun getViewedItems(callback: (List<ProductUiModel>) -> Unit)

    fun getLastViewedItem(callback: (ProductUiModel?) -> Unit)
}
