package woowacourse.shopping.presentation.ui.curation.model

import woowacourse.shopping.presentation.ui.payment.model.PaymentUiModel

sealed interface CurationNavigation {
    data class ToPayment(val paymentUiModel: PaymentUiModel) : CurationNavigation
}
