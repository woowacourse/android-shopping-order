package woowacourse.shopping.ui.products.uimodel

data class ProductWithQuantityUiState(
    val productWithQuantities: List<ProductUiModel>,
    val isLoading: Boolean = true,
) {
    companion object {
        val DEFAULT: ProductWithQuantityUiState = ProductWithQuantityUiState(emptyList(), true)
    }
}
