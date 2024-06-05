package woowacourse.shopping.ui.cart.item

data class CartItemsUiState(
    val cartItems: List<CartUiModel>,
    val isLoading: Boolean = true,
)
