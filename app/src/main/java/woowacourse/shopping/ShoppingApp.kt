package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.data.cart.CartItemDataSource
import woowacourse.shopping.data.cart.CartItemRemoteDataSource
import woowacourse.shopping.data.history.ProductHistoryDataSource
import woowacourse.shopping.data.history.ProductHistoryLocalDataSource
import woowacourse.shopping.data.order.OrderRemoteDataSource
import woowacourse.shopping.data.product.ProductDataSource
import woowacourse.shopping.data.product.ProductRemoteDataSource
import woowacourse.shopping.local.history.HistoryProductDao
import woowacourse.shopping.local.history.HistoryProductDatabase
import woowacourse.shopping.remote.cart.CartItemApiService
import woowacourse.shopping.remote.common.RetrofitService
import woowacourse.shopping.remote.order.OrderApiService
import woowacourse.shopping.remote.product.ProductsApiService

class ShoppingApp : Application() {
    private val productsApi: ProductsApiService by lazy {
        RetrofitService.retrofitService.create(
            ProductsApiService::class.java,
        )
    }
    private val cartItemApi: CartItemApiService by lazy {
        RetrofitService.retrofitService.create(
            CartItemApiService::class.java,
        )
    }
    private val orderApi: OrderApiService by lazy {
        RetrofitService.retrofitService.create(
            OrderApiService::class.java,
        )
    }

    private val historyProductDb: HistoryProductDatabase by lazy { HistoryProductDatabase.database(context = this) }
    private val historyProductDao: HistoryProductDao by lazy { historyProductDb.dao() }

    override fun onCreate() {
        super.onCreate()
        productSource = ProductRemoteDataSource(productsApi)
        cartSource = CartItemRemoteDataSource(cartItemApi)
        historySource = ProductHistoryLocalDataSource(historyProductDao)
        orderSource = OrderRemoteDataSource(orderApi)
    }

    override fun onTerminate() {
        super.onTerminate()
        productSource.shutDown()
        historyProductDb.close()
    }

    companion object {
        lateinit var productSource: ProductDataSource
            private set

        lateinit var cartSource: CartItemDataSource
            private set

        lateinit var historySource: ProductHistoryDataSource
            private set

        lateinit var orderSource: OrderRemoteDataSource
            private set
    }
}
