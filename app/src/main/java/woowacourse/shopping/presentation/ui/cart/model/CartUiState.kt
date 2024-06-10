package woowacourse.shopping.presentation.ui.cart.model

data class CartUiState(
    val cartProductUiModels: List<CartProductUiModel> = emptyList(),
    val isLoading: Boolean = true,
)
