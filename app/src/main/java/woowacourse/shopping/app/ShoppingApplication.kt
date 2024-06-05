package woowacourse.shopping.app

import android.app.Application
import woowacourse.shopping.data.local.recent.RecentProductDatabase
import woowacourse.shopping.data.remote.service.CartService
import woowacourse.shopping.data.remote.service.OrderService
import woowacourse.shopping.data.remote.service.ProductService
import woowacourse.shopping.data.remote.datasource.CartDataSourceImpl
import woowacourse.shopping.data.remote.datasource.OrderDataSourceImpl
import woowacourse.shopping.data.remote.datasource.ProductDataSourceImpl
import woowacourse.shopping.data.remote.RetrofitClient

class ShoppingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val retrofit = RetrofitClient.getInstance()
        val productService = retrofit.create(ProductService::class.java)
        val cartService = retrofit.create(CartService::class.java)
        val orderService = retrofit.create(OrderService::class.java)

        cartDataSourceImpl = CartDataSourceImpl(cartService)
        productDataSourceImpl = ProductDataSourceImpl(productService)
        orderDataSourceImpl = OrderDataSourceImpl(orderService)
        recentProductDatabase = RecentProductDatabase.getInstance(this)
    }

    companion object {
        lateinit var recentProductDatabase: RecentProductDatabase
        lateinit var cartDataSourceImpl: CartDataSourceImpl
        lateinit var productDataSourceImpl: ProductDataSourceImpl
        lateinit var orderDataSourceImpl: OrderDataSourceImpl
    }
}
