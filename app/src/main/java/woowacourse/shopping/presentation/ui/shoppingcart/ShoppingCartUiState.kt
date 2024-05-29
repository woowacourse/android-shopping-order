package woowacourse.shopping.presentation.ui.shoppingcart

import woowacourse.shopping.domain.model.Cart

data class ShoppingCartUiState(
    val pagingCartProduct: PagingCartProduct = PagingCartProduct(),
)

data class PagingCartProduct(
    val cartList: List<Cart> = emptyList(),
    val currentPage: Int = 0,
    val last: Boolean = true,
)
