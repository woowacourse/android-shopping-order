package woowacourse.shopping.presentation.ui.payment.model

import woowacourse.shopping.presentation.common.UpdateUiModel

sealed interface PaymentNavigation {
    data class ToShopping(val updateUiModel: UpdateUiModel) : PaymentNavigation
}
