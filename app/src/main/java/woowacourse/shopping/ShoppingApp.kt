package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.data.source.OrderDataSource
import woowacourse.shopping.data.source.ProductDataSource
import woowacourse.shopping.data.source.ProductHistoryDataSource
import woowacourse.shopping.data.source.ShoppingCartDataSource
import woowacourse.shopping.local.history.HistoryProductDao
import woowacourse.shopping.local.history.HistoryProductDatabase
import woowacourse.shopping.local.source.LocalHistoryProductDataSource
import woowacourse.shopping.remote.RetrofitService
import woowacourse.shopping.remote.service.CartItemApiService
import woowacourse.shopping.remote.service.OrderApiService
import woowacourse.shopping.remote.service.ProductsApiService
import woowacourse.shopping.remote.source.CartItemRemoteDataSource
import woowacourse.shopping.remote.source.OrderRemoteDataSource
import woowacourse.shopping.remote.source.ProductRemoteDataSource

class ShoppingApp : Application() {
    private val productsApi: ProductsApiService by lazy { RetrofitService.retrofitService.create(ProductsApiService::class.java) }
    private val cartItemApi: CartItemApiService by lazy { RetrofitService.retrofitService.create(CartItemApiService::class.java) }
    private val orderApi: OrderApiService by lazy { RetrofitService.retrofitService.create(OrderApiService::class.java) }

    private val historyProductDb: HistoryProductDatabase by lazy { HistoryProductDatabase.database(context = this) }
    private val historyProductDao: HistoryProductDao by lazy { historyProductDb.dao() }

    override fun onCreate() {
        super.onCreate()
        productSource = ProductRemoteDataSource(productsApi)
        cartSource = CartItemRemoteDataSource(cartItemApi)
        historySource = LocalHistoryProductDataSource(historyProductDao)
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

        lateinit var cartSource: ShoppingCartDataSource
            private set

        lateinit var historySource: ProductHistoryDataSource
            private set

        lateinit var orderSource: OrderDataSource
            private set
    }
}
