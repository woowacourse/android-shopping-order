package woowacourse.shopping.presentation.ui.shoppingcart.orderrecommend

import woowacourse.shopping.domain.model.Cart

data class OrderRecommendUiState(
    val recommendCarts: List<Cart> = emptyList(),
    val orderCarts: MutableMap<Int, Cart> = hashMapOf(),
) {
    val orderTotalPrice get() = orderCarts.values.sumOf { it.totalPrice }

    val totalQuantity get() = orderCarts.values.sumOf { it.quantity }
}
