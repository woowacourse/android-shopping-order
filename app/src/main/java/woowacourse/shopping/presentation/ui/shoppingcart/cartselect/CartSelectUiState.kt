package woowacourse.shopping.presentation.ui.shoppingcart.cartselect

import woowacourse.shopping.domain.model.Cart

data class CartSelectUiState(
    val pagingCartProduct: PagingCartProduct = PagingCartProduct(),
    val orderCartList: HashMap<Int, Cart> = hashMapOf(),
) {
    val orderTotalPrice get() = orderCartList.values.sumOf { it.totalPrice }
}

data class PagingCartProduct(
    val cartList: List<Cart> = emptyList(),
    val currentPage: Int = 0,
    val last: Boolean = true,
)
