package woowacourse.shopping.ui.state

import woowacourse.shopping.domain.PurchaseProducts

data class CartUiState(
    val items: PurchaseProducts = PurchaseProducts(),
    val allItems: PurchaseProducts = PurchaseProducts(),
    val totalCount: Int = 0,
    val currentPage: Int = 0,
    val checkedItemIds: List<Long> = emptyList(),
    val totalPrice: Int = 0,
    val isAllChecked: Boolean = false,
    val isLoading: Boolean = false,
    val isNextEnable: Boolean = false,
    val isPrevEnable: Boolean = false,
    val isPageable: Boolean = false,
    val errorMessage: String? = null
)
