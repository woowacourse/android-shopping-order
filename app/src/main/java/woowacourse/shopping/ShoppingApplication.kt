package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.di.DatabaseProvider

class ShoppingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DatabaseProvider.init(applicationContext)
    }
}
