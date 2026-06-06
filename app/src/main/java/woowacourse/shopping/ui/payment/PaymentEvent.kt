package woowacourse.shopping.ui.payment

import android.content.Context
import woowacourse.shopping.R

sealed interface PaymentEvent {
    sealed interface Message {
        fun toDisplayString(context: Context): String

        object OrderSuccess : Message {
            override fun toDisplayString(context: Context): String = context.getString(R.string.msg_order_success)
        }

        data class NetworkError(
            val code: Int,
        ) : Message {
            override fun toDisplayString(context: Context): String = "${context.getString(R.string.msg_network_error)}$code"
        }

        data class ExceptionError(
            val message: String?,
        ) : Message {
            override fun toDisplayString(context: Context): String = "${context.getString(R.string.msg_generic_error)}$message"
        }
    }

    data class SnackbarEvent(
        val message: Message,
    ) : PaymentEvent

    object Order : PaymentEvent

    object NavigateBack : PaymentEvent

    object NavigateToShopping : PaymentEvent
}
