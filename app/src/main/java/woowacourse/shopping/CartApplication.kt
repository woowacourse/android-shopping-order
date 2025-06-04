package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.data.AuthStorage
import woowacourse.shopping.data.product.source.LocalRecentViewedProductsDataSource

class CartApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        LocalRecentViewedProductsDataSource.init(this)
        AuthStorage.init(this)
    }
}
