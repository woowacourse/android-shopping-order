package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.data.repository.RepositoryProvider

class ShoppingApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        RepositoryProvider.initialize(this)
    }
}
