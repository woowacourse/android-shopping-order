package woowacourse.shopping.ui.cart.cartitem

data class CartItemsUiState(
    val cartItems: List<CartUiModel>,
    val isLoading: Boolean = true,
)
