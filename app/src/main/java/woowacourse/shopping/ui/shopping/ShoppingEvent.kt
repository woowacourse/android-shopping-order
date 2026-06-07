package woowacourse.shopping.ui.shopping

import android.content.Context
import woowacourse.shopping.R
import woowacourse.shopping.domain.PurchaseProduct

sealed interface ShoppingEvent {
    sealed interface Message {
        fun toDisplayString(context: Context): String

        object CartAdded : Message {
            override fun toDisplayString(context: Context): String = context.getString(R.string.msg_cart_added)
        }

        object QuantityUpdated : Message {
            override fun toDisplayString(context: Context): String = context.getString(R.string.msg_quantity_updated)
        }

        object RecentProductsLoadFailed : Message {
            override fun toDisplayString(context: Context): String = context.getString(R.string.msg_recent_products_load_failed)
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

    data class ShowSnackBar(
        val message: Message,
    ) : ShoppingEvent

    data class NavigateToProductDetail(
        val selectedProductId: Long,
        val lastViewedProductId: Long? = null,
    ) : ShoppingEvent

    object NavigateToCart : ShoppingEvent

    data class AddToCart(
        val purchaseProduct: PurchaseProduct,
    ) : ShoppingEvent

    data class UpdateCount(
        val productID: Long,
        val updateAmount: Int,
    ) : ShoppingEvent

    data class RemoveFromCart(
        val purchaseProductId: Long,
    ) : ShoppingEvent

    object LoadMore : ShoppingEvent
}
