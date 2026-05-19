package woowacourse.shopping.ui.cart

import woowacourse.shopping.ui.cart.uimodel.CartInfo

data class CartUiState(
    val cartItems: List<CartInfo> = emptyList(),
    val currentPage: Int = 0,
    val isPageable: Boolean = false,
    val previousEnable: Boolean = false,
    val nextEnable: Boolean = false,
    val isLoading: Boolean = false,
    val totalPrice: Int = 0,
    val totalCount: Int = 0,
    val checkedItemIds: List<Long> = emptyList(),
) {
    fun isChecked(id: Long): Boolean = id in checkedItemIds
}
