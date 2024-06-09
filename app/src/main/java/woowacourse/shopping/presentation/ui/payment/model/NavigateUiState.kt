package woowacourse.shopping.presentation.ui.payment.model

import woowacourse.shopping.presentation.ui.UpdateUiModel

sealed interface NavigateUiState {
    data class ToShopping(val updateUiModel: UpdateUiModel) : NavigateUiState
}
