package woowacourse.shopping.presentation.ui.shoppingcart.orderrecommend

import woowacourse.shopping.domain.model.Cart

data class OrderRecommendUiState(
    val recommendCarts: List<Cart> = emptyList(),
    val orderCarts: List<Cart> = emptyList(),
) {
    val orderTotalPrice get() = orderCarts.sumOf { it.totalPrice }

    val totalQuantity get() = orderCarts.sumOf { it.quantity }
}
