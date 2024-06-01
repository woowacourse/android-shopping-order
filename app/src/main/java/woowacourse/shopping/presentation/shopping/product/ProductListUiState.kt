package woowacourse.shopping.presentation.shopping.product

import woowacourse.shopping.presentation.shopping.detail.ProductUi

data class ProductListUiState(
    val currentPage: Int = 1,
    val isLoading: Boolean = false,
    val totalProducts: List<ShoppingUiModel> = emptyList(),
    val recentProducts: List<ProductUi> = emptyList(),
) {
    val products: List<ShoppingUiModel.Product>
        get() = totalProducts.filterIsInstance<ShoppingUiModel.Product>()

    val loadMoreModels: List<ShoppingUiModel.LoadMore>
        get() = totalProducts.filterIsInstance<ShoppingUiModel.LoadMore>()
}
