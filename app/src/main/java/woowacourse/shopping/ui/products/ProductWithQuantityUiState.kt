package woowacourse.shopping.ui.products

data class ProductWithQuantityUiState(
    val productWithQuantities: List<ProductUiModel>,
    val isLoading: Boolean = true,
)
