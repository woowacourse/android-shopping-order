package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.di.AuthStorageModule
import woowacourse.shopping.di.DatabaseModule

class CartApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AuthStorageModule.init(this)
        DatabaseModule.init(this)
    }
}
