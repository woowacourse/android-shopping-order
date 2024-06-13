package woowacourse.shopping.app

import android.app.Application
import woowacourse.shopping.local.datasource.LocalRecentDataSource
import woowacourse.shopping.local.db.RecentProductDatabase
import woowacourse.shopping.data.client.RetrofitClient
import woowacourse.shopping.remote.datasource.RemoteCartDataSource
import woowacourse.shopping.remote.datasource.RemoteOrderDataSource
import woowacourse.shopping.remote.datasource.RemotePaymentDataSource
import woowacourse.shopping.remote.datasource.RemoteProductDataSource
import woowacourse.shopping.remote.service.CartService
import woowacourse.shopping.remote.service.OrderService
import woowacourse.shopping.remote.service.PaymentService
import woowacourse.shopping.remote.service.ProductService

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
        localRecentDataSource =
            LocalRecentDataSource(RecentProductDatabase.getInstance(this).recentProductDao())
    }

    companion object {
        lateinit var remoteCartDataSource: RemoteCartDataSource
        lateinit var remoteProductDataSource: RemoteProductDataSource
        lateinit var remoteOrderDataSource: RemoteOrderDataSource
        lateinit var remotePaymentDataSource: RemotePaymentDataSource
        lateinit var localRecentDataSource: LocalRecentDataSource
    }
}
