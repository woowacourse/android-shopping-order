package woowacourse.shopping.ui.state

import woowacourse.shopping.model.ShoppingItem

data class ProductListUiState(
    val shoppingItems: List<ShoppingItem> = emptyList(),
    val recentViewedShoppingItems: List<ShoppingItem> = emptyList(),
    val shoppingCartTotalCount: Int = 0,
    val isNetworkConnected: Boolean = true,
    val canLoadNextPage: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val hasError: Boolean get() = errorMessage != null
}
