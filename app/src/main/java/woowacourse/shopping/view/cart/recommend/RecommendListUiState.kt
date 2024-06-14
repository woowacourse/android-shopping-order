package woowacourse.shopping.view.cart.recommend

import woowacourse.shopping.view.home.product.HomeViewItem

data class RecommendListUiState(
    val isLoading: Boolean = true,
    val recommendedProducts: List<HomeViewItem.ProductViewItem> = emptyList(),
)
