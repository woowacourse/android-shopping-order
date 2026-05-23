package woowacourse.shopping.feature.recommend

sealed interface OrderDialogUiState {
    data object None : OrderDialogUiState
    data object AuthExpired : OrderDialogUiState
    data object ServerError : OrderDialogUiState
    data object NetworkError : OrderDialogUiState
}
