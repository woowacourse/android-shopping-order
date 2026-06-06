package woowacourse.shopping.ui.recommendation

import android.content.Context
import woowacourse.shopping.R
import woowacourse.shopping.domain.PurchaseProduct

sealed interface RecommendationEvent {
    sealed interface Message {
        fun toDisplayString(context: Context): String

        object CartAdded : Message {
            override fun toDisplayString(context: Context): String = context.getString(R.string.msg_cart_added)
        }

        object QuantityUpdated : Message {
            override fun toDisplayString(context: Context): String = context.getString(R.string.msg_quantity_updated)
        }

        object CartRemoved : Message {
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
    ) : RecommendationEvent

    data class AddToCart(
        val purchaseProduct: PurchaseProduct,
    ) : RecommendationEvent

    data class UpdateAmount(
        val targetID: Long,
        val updateAmount: Int,
    ) : RecommendationEvent

    data class RemoveFromCart(
        val targetId: Long,
    ) : RecommendationEvent

    data class NavigateToPayment(
        val checkedIds: List<Long>,
    ) : RecommendationEvent

    object NavigateToCart : RecommendationEvent
}
