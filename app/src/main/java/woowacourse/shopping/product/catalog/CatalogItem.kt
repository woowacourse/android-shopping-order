package woowacourse.shopping.product.catalog

import woowacourse.shopping.domain.LoadingState

sealed class CatalogItem {
    data class ProductItem(
        val productItem: ProductUiModel,
    ) : CatalogItem()

    data object LoadMoreButtonItem : CatalogItem()

    data class LoadingStateProductItem(
        val loadingState: LoadingState,
    ) : CatalogItem()
}
