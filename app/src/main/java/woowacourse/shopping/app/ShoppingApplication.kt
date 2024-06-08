package woowacourse.shopping.app

import android.app.Application
import woowacourse.shopping.data.local.recent.RecentProductDatabase
import woowacourse.shopping.data.remote.RetrofitClient
import woowacourse.shopping.data.remote.datasource.RemoteCartDataSource
import woowacourse.shopping.data.remote.datasource.RemoteOrderDataSource
import woowacourse.shopping.data.remote.datasource.RemotePaymentDataSource
import woowacourse.shopping.data.remote.datasource.RemoteProductDataSource
import woowacourse.shopping.data.remote.service.CartService
import woowacourse.shopping.data.remote.service.OrderService
import woowacourse.shopping.data.remote.service.PaymentService
import woowacourse.shopping.data.remote.service.ProductService

class ShoppingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val retrofit = RetrofitClient.getInstance()
        val productService = retrofit.create(ProductService::class.java)
        val cartService = retrofit.create(CartService::class.java)
        val orderService = retrofit.create(OrderService::class.java)
        val paymentService = retrofit.create(PaymentService::class.java)

        remoteCartDataSource = RemoteCartDataSource(cartService)
        remoteProductDataSource = RemoteProductDataSource(productService)
        remoteOrderDataSource = RemoteOrderDataSource(orderService)
        remotePaymentDataSource = RemotePaymentDataSource(paymentService)
        recentProductDatabase = RecentProductDatabase.getInstance(this)
    }

    companion object {
        lateinit var remoteCartDataSource: RemoteCartDataSource
        lateinit var remoteProductDataSource: RemoteProductDataSource
        lateinit var remoteOrderDataSource: RemoteOrderDataSource
        lateinit var remotePaymentDataSource: RemotePaymentDataSource
        lateinit var recentProductDatabase: RecentProductDatabase
    }
}
