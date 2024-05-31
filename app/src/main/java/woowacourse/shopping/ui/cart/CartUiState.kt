package woowacourse.shopping.ui.cart

sealed class CartUiState {
    class Success(val cartUiModels: List<CartUiModel>) : CartUiState()

    data object Empty : CartUiState()

    data object Loading : CartUiState()

    data object Failure : CartUiState()
}
