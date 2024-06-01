package woowacourse.shopping.presentation.ui.shoppingcart.cartselect

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import woowacourse.shopping.domain.model.Cart

data class CartSelectUiState(
    val pagingCartProduct: PagingCartProduct = PagingCartProduct(),
    val orderCarts: List<Cart> = emptyList(),
    val totalElements: Int = 0,
) {
    val orderTotalPrice get() = orderCarts.sumOf { it.totalPrice }

    val isAllChecked get() = totalElements == orderCarts.size

    val totalQuantity get() = orderCarts.sumOf { it.quantity }
}

data class PagingCartProduct(
    val cartList: List<CartProduct> = emptyList(),
    val currentPage: Int = 0,
    val last: Boolean = true,
)

@Parcelize
data class CartProduct(
    val cart: Cart,
    val isChecked: Boolean,
) : Parcelable

fun Cart.toCartProduct(isChecked: Boolean = false): CartProduct {
    return CartProduct(
        cart = this,
        isChecked = isChecked,
    )
}
