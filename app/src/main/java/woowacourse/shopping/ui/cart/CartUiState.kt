package woowacourse.shopping.ui.cart

import woowacourse.shopping.domain.PurchaseProducts

data class CartUiState(
    val items: PurchaseProducts = PurchaseProducts(),
    val currentPage: Int = 0,
    val checkedItemIds: List<Long> = emptyList(),
    val totalPrice: Int = 0,
    val isLoading: Boolean = false,
    val isNextEnable: Boolean = false,
    val isPrevEnable: Boolean = false,
    val isPageable: Boolean = false,
)
