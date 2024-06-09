package woowacourse.shopping.view.cart.list

data class CartListUiState(
    val isLoading: Boolean = true,
    val currentPageIndex: Int = 0,
    val cartViewItems: List<ShoppingCartViewItem.CartViewItem> = emptyList(),
    val previousPageEnabled: Boolean = true,
    val nextPageEnabled: Boolean = true,
)
