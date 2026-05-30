package woowacourse.shopping.ui.shopping

import woowacourse.shopping.model.product.Product

data class ShoppingUiState(
    val productListState: ProductListUiState = ProductListUiState.Loading,
    val recentProducts: List<Product> = emptyList(),
    val cartQuantity: Int = 0,
    val isNetworkConnected: Boolean = true,
)

sealed interface ProductListUiState {
    data object Loading : ProductListUiState

    data class Content(
        val products: List<ShoppingProductUiState>,
        val hasNext: Boolean,
    ) : ProductListUiState

    data class Error(
        val message: String?,
    ) : ProductListUiState
}
