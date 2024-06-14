package woowacourse.shopping.view.order.state

sealed class CouponUiState {
    data object Applied : CouponUiState()

    data class Error(val errorMessage: String) : CouponUiState()

    data object Idle : CouponUiState()
}
