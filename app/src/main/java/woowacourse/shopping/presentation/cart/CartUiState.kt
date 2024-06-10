package woowacourse.shopping.presentation.cart

data class CartUiState(
    val cartUiModels: List<CartUiModel>,
    val isSuccess: Boolean = false,
    val isLoading: Boolean = true,
    val isFailure: Boolean = false,
)
