package woowacourse.shopping.presentation.productdetail.model

import woowacourse.shopping.presentation.common.model.ProductUiModel

sealed interface DetailUiState {
    object Loading : DetailUiState

    data class Success(
        val product: ProductUiModel,
        val quantity: Int,
        val recentProduct: ProductUiModel? = null,
    ) : DetailUiState {
        val price: Long get() = product.price * quantity
    }

    data class Error(
        val message: String,
    ) : DetailUiState
}
