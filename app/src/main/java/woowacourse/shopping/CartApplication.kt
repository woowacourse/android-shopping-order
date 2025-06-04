package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.data.AuthStorage
import woowacourse.shopping.data.product.source.LocalRecentViewedProductsDataSource

class CartApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        LocalRecentViewedProductsDataSource.init(this)
        instance = this
        authStorage = AuthStorage(this)
    }

    companion object {
        private lateinit var instance: CartApplication

        lateinit var authStorage: AuthStorage
            private set
    }
}
