package woowacourse.shopping.presentation.ui.shoppingcart.orderrecommend

import woowacourse.shopping.domain.model.Cart

sealed interface RecommendCartsUiState {
    data class Success(
        val recommendCarts: List<Cart> = emptyList(),
    ) : RecommendCartsUiState

    data object Loading : RecommendCartsUiState

    class Error(val message: String?) : RecommendCartsUiState
}

sealed interface OrderCartsUiState {
    data class Success(
        val orderCarts: MutableMap<Int, Cart> = hashMapOf(),
    ) : OrderCartsUiState {
        val orderTotalPrice get() = orderCarts.values.sumOf { it.totalPrice }

        val totalQuantity get() = orderCarts.values.sumOf { it.quantity }
    }

    data object Loading : OrderCartsUiState

    class Error(val message: String?) : OrderCartsUiState
}
