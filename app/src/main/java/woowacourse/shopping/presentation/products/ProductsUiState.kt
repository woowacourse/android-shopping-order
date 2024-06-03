package woowacourse.shopping.presentation.products

import woowacourse.shopping.presentation.products.adapter.type.ProductUiModel

data class ProductsUiState(
    val productUiModels: List<ProductUiModel>? = null,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
)
