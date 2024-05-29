package woowacourse.shopping.presentation.ui.shopping

import woowacourse.shopping.domain.CartProduct

sealed interface NavigateUiState {
    data class ToDetail(val cartProduct: CartProduct) : NavigateUiState

    data object ToCart : NavigateUiState
}
