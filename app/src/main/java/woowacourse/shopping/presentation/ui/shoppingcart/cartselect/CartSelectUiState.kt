package woowacourse.shopping.presentation.ui.shoppingcart.cartselect

import woowacourse.shopping.domain.model.Cart

data class CartSelectUiState(
    val pagingCartProduct: PagingCartProduct = PagingCartProduct(),
    val orderCarts: MutableMap<Int, Cart> = hashMapOf(),
    val totalElements: Int? = 0,
) {
    val orderTotalPrice get() = orderCarts.values.sumOf { it.totalPrice }

    val isAllChecked get() = (totalElements == orderCarts.size)

    val isAnyChecked get() = (orderCarts.values.isNotEmpty())

    val totalQuantity get() = orderCarts.values.sumOf { it.quantity }
}

data class PagingCartProduct(
    val carts: List<Cart> = emptyList(),
    val currentPage: Int = 0,
    val last: Boolean = true,
)
