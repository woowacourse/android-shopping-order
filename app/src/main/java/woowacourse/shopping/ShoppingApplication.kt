package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.data.db.recent.RecentProductDatabase
import woowacourse.shopping.data.remote.CartService
import woowacourse.shopping.data.remote.OrderService
import woowacourse.shopping.data.remote.ProductService
import woowacourse.shopping.data.remote.RemoteCartDataSource
import woowacourse.shopping.data.remote.RemoteOrderDataSource
import woowacourse.shopping.data.remote.RemoteProductDataSource
import woowacourse.shopping.data.remote.RetrofitClient

class ShoppingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val retrofit = RetrofitClient.getInstance()
        val productService = retrofit.create(ProductService::class.java)
        val cartService = retrofit.create(CartService::class.java)
        val orderService = retrofit.create(OrderService::class.java)

        remoteCartDataSource = RemoteCartDataSource(cartService)
        remoteProductDataSource = RemoteProductDataSource(productService)
        remoteOrderDataSource = RemoteOrderDataSource(orderService)
        recentProductDatabase = RecentProductDatabase.getInstance(this)
    }

    companion object {
        lateinit var recentProductDatabase: RecentProductDatabase
        lateinit var remoteCartDataSource: RemoteCartDataSource
        lateinit var remoteProductDataSource: RemoteProductDataSource
        lateinit var remoteOrderDataSource: RemoteOrderDataSource
    }
}
