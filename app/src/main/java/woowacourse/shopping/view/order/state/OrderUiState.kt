package woowacourse.shopping.view.order.state

sealed class OrderUiState {
    data object Success : OrderUiState()

    data class Failure(val errorMessage: String?) : OrderUiState()

    data object Idle : OrderUiState()
}
