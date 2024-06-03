package woowacourse.shopping.presentation.cart

sealed class CartUiState {
    class Success(val cartUiModels: List<CartUiModel>) : CartUiState()

    data object Loading : CartUiState()

    data object Failure : CartUiState()
}
