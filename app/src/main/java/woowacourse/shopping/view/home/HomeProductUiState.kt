package woowacourse.shopping.view.home

import woowacourse.shopping.domain.model.CartData
import woowacourse.shopping.view.home.product.HomeViewItem

data class HomeProductUiState(
    val isLoading: Boolean = true,
    val currentPageNumber: Int = 0,
    val productItems: List<HomeViewItem.ProductViewItem> = emptyList(),
    val cartItems: List<CartData> = emptyList(),
    val totalCartQuantity: Int = 0,
    val loadMoreAvailable: Boolean = false,
)
