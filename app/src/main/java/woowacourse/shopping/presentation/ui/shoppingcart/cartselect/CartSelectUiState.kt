package woowacourse.shopping.presentation.ui.shoppingcart.cartselect

import woowacourse.shopping.domain.model.Cart

data class CartSelectUiState(
    val pagingCartProduct: PagingCartProduct = PagingCartProduct(),
    val cartIdList: List<Int> = emptyList(),
)

data class PagingCartProduct(
    val cartList: List<Cart> = emptyList(),
    val currentPage: Int = 0,
    val last: Boolean = true,
)
