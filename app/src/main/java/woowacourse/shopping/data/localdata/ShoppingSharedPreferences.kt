package woowacourse.shopping.data.localdata

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit

class ShoppingSharedPreferences(
    context: Context,
) {
    private val sharedPreference = context.getSharedPreferences(NAME, MODE_PRIVATE)

    fun saveIsNotification(isNotification: Boolean) {
        sharedPreference.edit(commit = true) {
            putBoolean(NOTIFICATION_KEY, isNotification)
        }
    }

    fun getIsNotification(): Boolean = sharedPreference.getBoolean(NOTIFICATION_KEY, false)

    fun savePaymentCartItemIds(cartItemIds: List<String>) {
        sharedPreference.edit(commit = true) {
            putString(PAYMENT_CART_ITEM_IDS_KEY, cartItemIds.joinToString(DELIMITER))
        }
    }

    fun getPaymentCartItemIds(): List<String> =
        sharedPreference
            .getString(PAYMENT_CART_ITEM_IDS_KEY, null)
            ?.split(DELIMITER)
            ?.filter { it.isNotBlank() }
            ?: emptyList()

    fun clearPaymentCartItemIds() {
        sharedPreference.edit(commit = true) {
            remove(PAYMENT_CART_ITEM_IDS_KEY)
        }
    }

    companion object {
        private const val NAME = "setting"
        private const val NOTIFICATION_KEY = "notification"
        private const val PAYMENT_CART_ITEM_IDS_KEY = "payment_cart_item_ids"
        private const val DELIMITER = ","
    }
}
