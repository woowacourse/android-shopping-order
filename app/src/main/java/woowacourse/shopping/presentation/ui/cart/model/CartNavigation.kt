package woowacourse.shopping.presentation.ui.cart.model

import woowacourse.shopping.presentation.ui.payment.model.PaymentUiModel

sealed interface CartNavigation {
    data class ToPayment(val paymentUiModel: PaymentUiModel) : CartNavigation
}
