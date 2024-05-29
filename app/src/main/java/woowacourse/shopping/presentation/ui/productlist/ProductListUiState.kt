package woowacourse.shopping.presentation.ui.productlist

import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Product

data class ProductListUiState(
    val pagingCart: PagingCart = PagingCart(),
    val productHistories: List<Product> = emptyList(),
    val cartQuantity: Int = 0,
) {
    val cartTotalCount: Int
        get() =
            if (cartQuantity >= MAX_CART_COUNT) {
                MAX_CART_COUNT
            } else {
                cartQuantity
            }

    companion object {
        const val MAX_CART_COUNT = 99
    }
}

data class PagingCart(
    val cartList: List<Cart> = emptyList(),
    val last: Boolean = false,
)
