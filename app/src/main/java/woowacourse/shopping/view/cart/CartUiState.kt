package woowacourse.shopping.view.cart

import woowacourse.shopping.domain.model.CartItemDomain

data class CartUiState(
    val isCheckboxVisible: Boolean = true,
    val isEntireCheckboxSelected: Boolean = false,
    val totalPrice: Int = 0,
    val isActivated: Boolean = false,
    val selectedCartItems: List<CartItemDomain> = emptyList(),
)
