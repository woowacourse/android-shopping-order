package woowacourse.shopping.presentation.ui.curation.model

import woowacourse.shopping.presentation.ui.payment.model.PaymentUiModel

sealed interface NavigateUiState {
    data class ToPayment(val paymentUiModel: PaymentUiModel): NavigateUiState
}