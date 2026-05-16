package woowacourse.shopping

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import woowacourse.shopping.di.RepositoryProvider

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_token")

class ShoppingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RepositoryProvider.init(
            context = this,
            id = "aksworns22",
            password = "password",
        )
    }
}
