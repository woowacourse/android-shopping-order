package woowacourse.shopping.presentation.ui.shoppingcart.cartselect

import woowacourse.shopping.domain.model.Cart

data class CartSelectUiState(
    val pagingCartProduct: PagingCartProduct = PagingCartProduct(),
    val orderCarts: List<Cart> = emptyList(),
    val totalElements: Int = 0,
) {
    val orderTotalPrice get() = orderCarts.sumOf { it.totalPrice }

    val isAllChecked get() = totalElements == orderCarts.size

    val totalQuantity get() = orderCarts.sumOf { it.quantity }

    val showPageIndicator: Boolean get() = !(pagingCartProduct.currentPage == 0 && pagingCartProduct.last)
}

data class PagingCartProduct(
    val cartProducts: List<CartProduct> = emptyList(),
    val currentPage: Int = 0,
    val last: Boolean = true,
)

data class CartProduct(
    val cart: Cart,
    val isChecked: Boolean,
)

fun Cart.toCartProduct(isChecked: Boolean = false): CartProduct {
    return CartProduct(
        cart = this,
        isChecked = isChecked,
    )
}
