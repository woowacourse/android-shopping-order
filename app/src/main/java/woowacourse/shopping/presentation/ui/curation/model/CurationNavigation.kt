package woowacourse.shopping.presentation.ui.curation.model

import woowacourse.shopping.presentation.ui.payment.model.PaymentUiState

sealed interface CurationNavigation {
    data class ToPayment(val paymentUiModel: PaymentUiState) : CurationNavigation
}
