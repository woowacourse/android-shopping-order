package woowacourse.shopping.presentation.ui.shoppingcart.cartselect

import woowacourse.shopping.domain.model.Cart

data class CartSelectUiState(
    val pagingCartProduct: PagingCartProduct = PagingCartProduct(),
    val orderCartList: MutableMap<Int, Cart> = hashMapOf(),
    val totalElements: Int = 0,
) {
    val orderTotalPrice get() = orderCartList.values.sumOf { it.totalPrice }

    val isAllChecked get() = (totalElements == orderCartList.size)

    val isAnyChecked get() = (orderCartList.values.isNotEmpty())

    val totalQuantity get() = orderCartList.values.sumOf { it.quantity }
}

data class PagingCartProduct(
    val cartList: List<Cart> = emptyList(),
    val currentPage: Int = 0,
    val last: Boolean = true,
)
