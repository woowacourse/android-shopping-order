package woowacourse.shopping.presentation.ui.cart.model

import woowacourse.shopping.presentation.ui.payment.model.PaymentUiState

sealed interface CartNavigation {
    data class ToPayment(val paymentUiModel: PaymentUiState) : CartNavigation
}
