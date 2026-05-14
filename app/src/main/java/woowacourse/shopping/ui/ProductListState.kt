package woowacourse.shopping.ui

import woowacourse.shopping.model.ShoppingItem

data class ProductListState(
    val isLoading: Boolean = false,
    val products: List<ShoppingItem> = emptyList(),
    val errorMessage: String? = null,
)
