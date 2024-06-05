package woowacourse.shopping.ui.products.uimodel

import woowacourse.shopping.ui.products.ProductUiModel

data class ProductWithQuantityUiState(
    val productWithQuantities: List<ProductUiModel>,
    val isLoading: Boolean = true,
) {
    companion object {
        val DEFAULT: ProductWithQuantityUiState = ProductWithQuantityUiState(emptyList(), true)
    }
}
