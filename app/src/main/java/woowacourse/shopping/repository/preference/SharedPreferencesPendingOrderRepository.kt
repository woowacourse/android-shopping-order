package woowacourse.shopping.repository.preference

import android.content.Context
import android.content.SharedPreferences
import kotlinx.serialization.json.Json
import woowacourse.shopping.repository.PendingOrderRepository
import woowacourse.shopping.ui.cart.SelectedCartOrder

private const val PREFERENCE_NAME = "shopping_pending_order"
private const val KEY_PENDING_ORDER = "pending_order"

class SharedPreferencesPendingOrderRepository(
    private val sharedPreferences: SharedPreferences,
    private val json: Json = Json,
) : PendingOrderRepository {
    override fun getPendingOrder(): SelectedCartOrder? =
        sharedPreferences
            .getString(KEY_PENDING_ORDER, null)
            ?.let { savedOrder ->
                runCatching {
                    json.decodeFromString<SelectedCartOrder>(savedOrder)
                }.getOrNull()
            }

    override fun savePendingOrder(order: SelectedCartOrder) {
        sharedPreferences
            .edit()
            .putString(KEY_PENDING_ORDER, json.encodeToString(order))
            .apply()
    }

    override fun clearPendingOrder() {
        sharedPreferences
            .edit()
            .remove(KEY_PENDING_ORDER)
            .apply()
    }

    companion object {
        fun create(context: Context): SharedPreferencesPendingOrderRepository =
            SharedPreferencesPendingOrderRepository(
                sharedPreferences =
                    context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE),
            )
    }
}
