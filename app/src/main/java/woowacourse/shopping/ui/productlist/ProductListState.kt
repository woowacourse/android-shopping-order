package woowacourse.shopping.ui.productlist

import woowacourse.shopping.domain.model.ShoppingItem

data class ProductListState(
    val isLoading: Boolean = false,
    val products: List<ShoppingItem> = emptyList(),
    val errorMessage: String? = null,
)
