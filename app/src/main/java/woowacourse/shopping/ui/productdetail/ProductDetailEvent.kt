package woowacourse.shopping.ui.productdetail

import android.content.Context
import woowacourse.shopping.R

sealed interface ProductDetailEvent {
    sealed interface Message {
        fun toDisplayString(context: Context): String

        data class NetworkError(val code: Int) : Message {
            override fun toDisplayString(context: Context): String =
                "${context.getString(R.string.msg_network_error)}$code"
        }

        data class ExceptionError(val message: String?) : Message {
            override fun toDisplayString(context: Context): String =
                "${context.getString(R.string.msg_generic_error)}${message}"
        }
    }

    data class SnackbarEvent(
        val errorMsg: Message,
    ) : ProductDetailEvent

    data class MoveToLastViewedProductDetail(
        val lastViewedProductId: Long,
    ) : ProductDetailEvent

    object MoveToShopping : ProductDetailEvent
}
