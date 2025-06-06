package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.di.DependencyProvider

class ShoppingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DependencyProvider.init(applicationContext)
    }
}
