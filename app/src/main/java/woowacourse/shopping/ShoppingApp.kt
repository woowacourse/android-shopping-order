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
import woowacourse.shopping.remote.common.RetrofitClient.cartItemApi
import woowacourse.shopping.remote.common.RetrofitClient.orderApi
import woowacourse.shopping.remote.common.RetrofitClient.productsApi

class ShoppingApp : Application() {
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
