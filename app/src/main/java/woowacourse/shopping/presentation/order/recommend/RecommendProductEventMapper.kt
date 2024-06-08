package woowacourse.shopping.presentation.order.recommend

import android.content.Context
import woowacourse.shopping.R

fun RecommendProductErrorEvent.toErrorMessageFrom(context: Context): String {
    return when (this) {
        RecommendProductErrorEvent.OrderProducts -> context.getString(R.string.error_msg_order_products)
        RecommendProductErrorEvent.IncreaseCartProduct -> context.getString(R.string.error_msg_increase_cart_count)
        RecommendProductErrorEvent.DecreaseCartProduct -> context.getString(R.string.error_msg_decrease_cart_count)
    }
}
