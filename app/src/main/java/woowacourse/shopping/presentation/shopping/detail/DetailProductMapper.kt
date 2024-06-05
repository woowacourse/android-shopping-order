package woowacourse.shopping.presentation.shopping.detail

import android.content.Context
import woowacourse.shopping.R

fun ProductDetailErrorEvent.toErrorMessageFrom(context: Context): String {
    return when (this) {
        ProductDetailErrorEvent.LoadProduct -> context.getString(R.string.error_msg_load_cart_products)
        ProductDetailErrorEvent.AddCartProduct -> context.getString(R.string.error_msg_update_cart_products)
        ProductDetailErrorEvent.DecreaseCartCount -> context.getString(R.string.error_msg_decrease_cart_count_limit)
        ProductDetailErrorEvent.SaveRecentProduct -> context.getString(R.string.error_msg_save_recent_product)
    }
}
