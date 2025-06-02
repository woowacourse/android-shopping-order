package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.data.RepositoryProvider

class ShoppingApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        RepositoryProvider.initialize(this)
    }
}
