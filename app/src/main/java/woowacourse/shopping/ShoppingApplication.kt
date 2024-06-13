package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.data.local.db.cart.CartDatabase
import woowacourse.shopping.data.local.db.recentproduct.RecentProductDatabase

class ShoppingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RecentProductDatabase.initialize(this)
        CartDatabase.initialize(this)
    }
}
