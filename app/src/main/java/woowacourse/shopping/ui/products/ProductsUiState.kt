package woowacourse.shopping.ui.products

import woowacourse.shopping.ui.products.adapter.type.ProductUiModel

data class ProductsUiState(
    val productUiModels: List<ProductUiModel>? = null,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
)
