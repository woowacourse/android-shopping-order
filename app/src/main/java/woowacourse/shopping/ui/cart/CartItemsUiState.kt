package woowacourse.shopping.ui.cart

import woowacourse.shopping.data.cart.Cart

data class CartItemsUiState(
    val cartItems: List<Cart>,
    val isLoading: Boolean = true,
)
