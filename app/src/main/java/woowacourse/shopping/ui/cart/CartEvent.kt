package woowacourse.shopping.ui.cart

import android.content.Context
import woowacourse.shopping.R

sealed interface CartEvent {
    sealed interface Message {
        fun toDisplayString(context: Context): String

        object QuantityUpdated : Message {
            override fun toDisplayString(context: Context): String = context.getString(R.string.msg_quantity_updated)
        }

        object RemoveFromCart : Message {
            override fun toDisplayString(context: Context): String = context.getString(R.string.msg_cart_removed)
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
        val errorMsg: Message,
    ) : CartEvent

    data class UpdateCount(
        val targetId: Long,
        val updateAmount: Int,
    ) : CartEvent

    data class RemoveFromCart(
        val targetId: Long,
    ) : CartEvent

    object NextPage : CartEvent

    object PrevPage : CartEvent

    object NavigateToShopping : CartEvent

    data class NavigateToRecommendation(
        val totalPrice: Int,
        val checkedIds: List<Long>,
    ) : CartEvent
}
