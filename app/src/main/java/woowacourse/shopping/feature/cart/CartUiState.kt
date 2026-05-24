package woowacourse.shopping.feature.cart

import woowacourse.shopping.feature.common.state.CartItemUiModel

data class CartUiState(
    val isLoading: Boolean = true,
    val page: Int = 1,
    val paginatedCartContents: List<CartItemUiModel> = emptyList(),
    val checkMap: Map<Long, Boolean> = emptyMap(),
    val totalPrice: Int = 0,
    val totalCount: Int = 0,
    val isFirstPage: Boolean = true,
    val isLastPage: Boolean = true,
)
