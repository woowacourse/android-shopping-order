package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.di.DatabaseModule

class ShoppingApp : Application() {
    override fun onCreate() {
        super.onCreate()
        DatabaseModule.init(this)
    }
}
