package woowacourse.shopping.ui.cart

data class ShoppingCartState(
    val selectedProductIds: Set<Long> = emptySet(),
    val isLoading: Boolean = false,
)
