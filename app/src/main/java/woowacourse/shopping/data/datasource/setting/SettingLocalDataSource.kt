package woowacourse.shopping.data.datasource.setting

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SettingLocalDataSource(
    private val dataStore: DataStore<Preferences>,
) : SettingDataSource {
    override suspend fun isPaymentNotificationEnabled(): Boolean {
        return dataStore.data.map { preference ->
            preference[PAYMENT_NOTIFICATION_KEY]
        }.first() ?: DEFAULT_PAYMENT_NOTIFICATION_ENABLED
    }

    override suspend fun setPaymentNotificationEnabled(enabled: Boolean) {
        dataStore.edit { preference ->
            preference[PAYMENT_NOTIFICATION_KEY] = enabled
        }
    }

    companion object {
        private val PAYMENT_NOTIFICATION_KEY = booleanPreferencesKey("payment_notification_enabled")
        private const val DEFAULT_PAYMENT_NOTIFICATION_ENABLED = false
    }
}
