package woowacourse.shopping.domain.repository

import woowacourse.shopping.presentation.product.catalog.ProductUiModel

interface ViewedItemRepository {
    suspend fun insertViewedItem(product: ProductUiModel)

    suspend fun getViewedItems(): List<ProductUiModel>

    suspend fun getLastViewedItem(): ProductUiModel?
}
