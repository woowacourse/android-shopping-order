package woowacourse.shopping.presentation.ui.payment.model

sealed interface NavigateUiState {

    data object ToShopping: NavigateUiState
}