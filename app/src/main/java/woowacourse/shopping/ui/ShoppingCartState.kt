package woowacourse.shopping.ui

import woowacourse.shopping.model.ShoppingCartItem

data class ShoppingCartState(
    val items: List<ShoppingCartItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val currentPage: Int = 0,
    val canMoveToPreviousPage: Boolean = false,
    val canMoveToNextPage: Boolean = false,
)
