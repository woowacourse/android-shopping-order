package woowacourse.shopping.ui.cart

data class CartItemsUiState(
    val cartItems: List<CartUiModel>,
    val isLoading: Boolean = true,
)
