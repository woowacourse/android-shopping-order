package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.data.source.CouponDataSource
import woowacourse.shopping.data.source.OrderDataSource2
import woowacourse.shopping.data.source.ProductDataSource
import woowacourse.shopping.data.source.ProductHistoryDataSource
import woowacourse.shopping.data.source.ShoppingCartDataSource
import woowacourse.shopping.local.history.HistoryProductDao
import woowacourse.shopping.local.history.HistoryProductDatabase
import woowacourse.shopping.local.source.LocalHistoryProductDataSource
import woowacourse.shopping.remote.RetrofitService
import woowacourse.shopping.remote.service.CartItemApiService
import woowacourse.shopping.remote.service.CouponApiService
import woowacourse.shopping.remote.service.OrderApiService
import woowacourse.shopping.remote.service.ProductsApiService
import woowacourse.shopping.remote.source.CartItemRemoteDataSource
import woowacourse.shopping.remote.source.CouponRemoteDataSource
import woowacourse.shopping.remote.source.DefaultOrderDataSource
import woowacourse.shopping.remote.source.ProductRemoteDataSource

class ShoppingApp : Application() {
    private val productsApi: ProductsApiService by lazy {
        RetrofitService.createRetorift(BuildConfig.BASE_PRODUCTS_URL_DEV).create(ProductsApiService::class.java)
    }
    private val cartItemApi: CartItemApiService by lazy {
        RetrofitService.createRetorift(BuildConfig.BASE_CART_ITEMS_URL_DEV).create(CartItemApiService::class.java)
    }
    private val orderApi: OrderApiService by lazy {
        RetrofitService.createRetorift(BuildConfig.BASE_ORDERS_URL_DEV).create(OrderApiService::class.java)
    }

    private val couponApiService: CouponApiService by lazy {
        RetrofitService.createRetorift(BuildConfig.BASE_COUPONS_URL_DEV).create(CouponApiService::class.java)
    }

    private val historyProductDb: HistoryProductDatabase by lazy { HistoryProductDatabase.database(context = this) }
    private val historyProductDao: HistoryProductDao by lazy { historyProductDb.dao() }

    override fun onCreate() {
        super.onCreate()
        productSource = ProductRemoteDataSource(productsApi)
        cartSource = CartItemRemoteDataSource(cartItemApi)
        historySource = LocalHistoryProductDataSource(historyProductDao)
        orderSource2 = DefaultOrderDataSource(orderApi)
        couponSource = CouponRemoteDataSource(couponApiService)
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

        lateinit var orderSource2: OrderDataSource2
            private set

        lateinit var couponSource: CouponDataSource
            private set
    }
}
