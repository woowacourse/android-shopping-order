package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.data.cart.remote.datasource.CartItemDataSource
import woowacourse.shopping.data.cart.remote.datasource.CartItemRemoteDataSource
import woowacourse.shopping.data.common.RetrofitClient.cartItemApi
import woowacourse.shopping.data.common.RetrofitClient.couponApi
import woowacourse.shopping.data.common.RetrofitClient.orderApi
import woowacourse.shopping.data.common.RetrofitClient.productsApi
import woowacourse.shopping.data.coupon.remote.datasource.CouponRemoteDataSource
import woowacourse.shopping.data.history.local.HistoryProductDao
import woowacourse.shopping.data.history.local.HistoryProductDatabase
import woowacourse.shopping.data.history.local.datasource.ProductHistoryDataSource
import woowacourse.shopping.data.history.local.datasource.ProductHistoryLocalDataSource
import woowacourse.shopping.data.order.remote.datasource.OrderRemoteDataSource
import woowacourse.shopping.data.product.remote.datasource.ProductDataSource
import woowacourse.shopping.data.product.remote.datasource.ProductRemoteDataSource

class ShoppingApp : Application() {
    private val historyProductDb: HistoryProductDatabase by lazy { HistoryProductDatabase.database(context = this) }
    private val historyProductDao: HistoryProductDao by lazy { historyProductDb.dao() }

    override fun onCreate() {
        super.onCreate()
        productSource = ProductRemoteDataSource(productsApi)
        cartSource = CartItemRemoteDataSource(cartItemApi)
        historySource = ProductHistoryLocalDataSource(historyProductDao)
        orderSource = OrderRemoteDataSource(orderApi)
        couponSource = CouponRemoteDataSource(couponApi)
    }

    override fun onTerminate() {
        super.onTerminate()
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

        lateinit var couponSource: CouponRemoteDataSource
            private set
    }
}
