package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.data.cart.CartDatabase
import woowacourse.shopping.data.recentproduct.RecentProductDatabase

class ShoppingApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        RecentProductDatabase.initialize(this)
        CartDatabase.initialize(this)
    }

}
