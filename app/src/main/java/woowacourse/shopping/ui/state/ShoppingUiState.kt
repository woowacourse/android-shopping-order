package woowacourse.shopping.ui.state

import woowacourse.shopping.domain.Products

data class ShoppingUiState(
    val products: Products = Products(),
    val recentlyViewedProducts: Products = Products(),
    val currentIndex: Int = 0,
    val cartItemCount: Int = 0,
    val isLoading: Boolean = false,
)