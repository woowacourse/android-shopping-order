package woowacourse.shopping.app

import android.app.Application
import woowacourse.shopping.local.database.AppDatabase

class ShoppingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppDatabase.init(this)
    }
}
