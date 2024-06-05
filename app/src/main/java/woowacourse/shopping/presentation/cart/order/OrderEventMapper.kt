package woowacourse.shopping.presentation.cart.order

import android.content.Context
import woowacourse.shopping.R

fun OrderErrorEvent.toErrorMessageFrom(context: Context): String {
    return when (this) {
        OrderErrorEvent.OrderProducts -> context.getString(R.string.error_msg_order_products)
        OrderErrorEvent.IncreaseCartProduct -> context.getString(R.string.error_msg_increase_cart_count)
        OrderErrorEvent.DecreaseCartProduct -> context.getString(R.string.error_msg_decrease_cart_count)
    }
}