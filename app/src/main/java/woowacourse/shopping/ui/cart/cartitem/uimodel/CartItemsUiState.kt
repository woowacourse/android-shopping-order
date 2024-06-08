package woowacourse.shopping.ui.cart.cartitem.uimodel

data class CartItemsUiState(
    val cartItems: List<CartUiModel>,
    val isLoading: Boolean = true,
)
