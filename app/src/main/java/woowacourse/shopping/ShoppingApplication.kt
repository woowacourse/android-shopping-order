package woowacourse.shopping

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import woowacourse.shopping.di.AppContainer
import woowacourse.shopping.di.DefaultAppContainer

class ShoppingApplication : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()

        val defaultAppContainer = DefaultAppContainer(this)
        appContainer = defaultAppContainer

        CoroutineScope(Dispatchers.IO).launch {
            defaultAppContainer.initializeDefaultUserAuth()
        }
    }
}
