package woowacourse.shopping.ui.productDetail

import woowacourse.shopping.domain.model.product.Product

sealed interface ProductDetailUiState {
    object Loading : ProductDetailUiState

    data class Success(
        val product: Product,
        val selectedQuantity: Int = 1,
        val lastViewedProduct: Product? = null,
    ) : ProductDetailUiState

    data class Error(
        val throwable: Throwable,
    ) : ProductDetailUiState
}
