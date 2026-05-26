package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.di.ShoppingRepositoryProvider

class ShoppingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ShoppingRepositoryProvider.initialize(this)
    }
}
