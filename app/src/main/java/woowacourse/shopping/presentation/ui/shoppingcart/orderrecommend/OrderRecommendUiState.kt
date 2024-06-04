package woowacourse.shopping.presentation.ui.shoppingcart.orderrecommend

import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Product

data class OrderRecommendUiState(
    val recommendProducts: List<Product> = emptyList(),
    val orderCarts: List<Cart> = emptyList(),
) {
    val orderTotalPrice get() = orderCarts.sumOf { it.totalPrice }

    val totalQuantity get() = orderCarts.sumOf { it.quantity }
}
