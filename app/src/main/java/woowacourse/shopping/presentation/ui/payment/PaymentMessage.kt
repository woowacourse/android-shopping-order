package woowacourse.shopping.presentation.ui.payment

import android.content.Context
import woowacourse.shopping.R
import woowacourse.shopping.presentation.base.MessageProvider

sealed interface PaymentMessage : MessageProvider {
    data object PaymentSuccessMessage : PaymentMessage {
        fun getMessage(context: Context): String = context.getString(R.string.payment_success_message)
    }
}
