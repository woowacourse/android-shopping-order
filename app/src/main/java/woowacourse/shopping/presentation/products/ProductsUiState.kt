package woowacourse.shopping.presentation.products

import woowacourse.shopping.presentation.products.uimodel.ProductUiModel

data class ProductsUiState(
    val productUiModels: List<ProductUiModel> = listOf(),
    val isLast: Boolean = false,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
)
