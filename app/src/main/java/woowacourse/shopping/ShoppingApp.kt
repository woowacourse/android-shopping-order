package woowacourse.shopping

import android.app.Application
import androidx.room.Index
import woowacourse.shopping.data.source.LocalHistoryProductDataSource
import woowacourse.shopping.data.source.ProductDataSource
import woowacourse.shopping.data.source.ProductHistoryDataSource
import woowacourse.shopping.data.source.ShoppingCartProductIdDataSource
import woowacourse.shopping.local.cart.ShoppingCartDao
import woowacourse.shopping.local.cart.ShoppingCartDatabase
import woowacourse.shopping.local.history.HistoryProductDao
import woowacourse.shopping.local.history.HistoryProductDatabase
import woowacourse.shopping.remote.CartItemApiService
import woowacourse.shopping.remote.CartItemRemoteDataSource
import woowacourse.shopping.remote.OrderApiService
import woowacourse.shopping.remote.OrderRemoteDataSource
import woowacourse.shopping.remote.ProductRemoteDataSource
import woowacourse.shopping.remote.ProductsApiService
import woowacourse.shopping.remote.RetrofitService

class ShoppingApp : Application() {
    private val productsApi: ProductsApiService by lazy { RetrofitService.retrofitService.create(ProductsApiService::class.java) }
    private val cartItemApi: CartItemApiService by lazy { RetrofitService.retrofitService.create(CartItemApiService::class.java) }
    private val orderApi: OrderApiService by lazy { RetrofitService.retrofitService.create(OrderApiService::class.java) }

    private val shoppingCartDb: ShoppingCartDatabase by lazy { ShoppingCartDatabase.database(context = this) }
    private val shoppingCartDao: ShoppingCartDao by lazy { shoppingCartDb.dao() }

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
        shoppingCartDb.close()
        historyProductDb.close()
    }

    companion object {
        lateinit var productSource: ProductDataSource
            private set

        lateinit var cartSource: ShoppingCartProductIdDataSource
            private set

        lateinit var historySource: ProductHistoryDataSource
            private set

        lateinit var orderSource: OrderRemoteDataSource
            private set
    }
}
