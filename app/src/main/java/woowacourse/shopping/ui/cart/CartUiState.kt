package woowacourse.shopping.ui.cart

import woowacourse.shopping.ui.products.adapter.type.ProductUiModel

sealed class CartUiState {
    class Success(val productUiModels: List<ProductUiModel>) : CartUiState()

    data object Loading : CartUiState()

    data object Failure : CartUiState()
}
