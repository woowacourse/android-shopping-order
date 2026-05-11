package woowacourse.shopping.storage.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private val Context.visitDataStore: DataStore<Preferences> by preferencesDataStore(name = "visit_datastore")

class DataStoreVisitStore(
    private val context: Context,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
) : VisitStore {
    override val recentVisitedProductIds: StateFlow<List<Long>> =
        context.visitDataStore.data
            .map { preferences -> parseProductIds(preferences[recentVisitedProductIdsKey]) }
            .stateIn(scope, SharingStarted.Eagerly, emptyList())

    override fun visit(productId: Long) {
        scope.launch {
            updateRecentVisitedProductIds { currentProductIds ->
                (listOf(productId) + currentProductIds.filterNot { id -> id == productId })
            }
        }
    }

    override fun removeVisitedProduct(productId: Long) {
        scope.launch {
            updateRecentVisitedProductIds { currentProductIds ->
                currentProductIds.filterNot { id -> id == productId }
            }
        }
    }

    private suspend fun updateRecentVisitedProductIds(transform: (List<Long>) -> List<Long>) {
        context.visitDataStore.edit { preferences ->
            val currentProductIds = parseProductIds(preferences[recentVisitedProductIdsKey])
            val updatedProductIds = transform(currentProductIds).distinct().take(MAX_RECENT_VISITS)
            if (updatedProductIds.isEmpty()) {
                preferences.remove(recentVisitedProductIdsKey)
                return@edit
            }
            preferences[recentVisitedProductIdsKey] = serializeProductIds(updatedProductIds)
        }
    }

    private fun parseProductIds(raw: String?): List<Long> =
        raw
            ?.split(ID_SEPARATOR)
            ?.mapNotNull { token -> token.toLongOrNull() }
            ?.distinct()
            ?.take(MAX_RECENT_VISITS)
            ?: emptyList()

    private fun serializeProductIds(productIds: List<Long>): String = productIds.joinToString(ID_SEPARATOR)

    private companion object {
        const val MAX_RECENT_VISITS = 10
        const val ID_SEPARATOR = ","
        val recentVisitedProductIdsKey = stringPreferencesKey("recent_visited_product_ids")
    }
}
