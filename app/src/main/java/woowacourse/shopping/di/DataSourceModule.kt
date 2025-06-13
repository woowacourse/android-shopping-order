package woowacourse.shopping.di

import android.content.Context
import woowacourse.shopping.data.datasource.local.RecentProductDataSource
import woowacourse.shopping.data.datasource.local.RecentProductDataSourceImpl
import woowacourse.shopping.data.datasource.remote.CartDataSource
import woowacourse.shopping.data.datasource.remote.CartDataSourceImpl
import woowacourse.shopping.data.datasource.remote.CouponDataSource
import woowacourse.shopping.data.datasource.remote.CouponDataSourceImpl
import woowacourse.shopping.data.datasource.remote.OrderDataSource
import woowacourse.shopping.data.datasource.remote.OrderDataSourceImpl
import woowacourse.shopping.data.datasource.remote.ProductDataSource
import woowacourse.shopping.data.datasource.remote.ProductDataSourceImpl

object DataSourceModule {
    private var cartDataSource: CartDataSource? = null
    private var productDataSource: ProductDataSource? = null
    private var recentProductDataSource: RecentProductDataSource? = null
    private var couponDataSource: CouponDataSource? = null
    private var orderDataSource: OrderDataSource? = null
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun provideProductDataSource(): ProductDataSource =
        productDataSource ?: run {
            val productService = NetworkModule.provideProductService()
            ProductDataSourceImpl(productService).also { productDataSource = it }
        }

    fun provideCartDataSource(): CartDataSource =
        cartDataSource ?: run {
            val cartItemService = NetworkModule.provideCartItemService()
            CartDataSourceImpl(cartItemService).also { cartDataSource = it }
        }

    fun provideCouponDataSource(): CouponDataSource =
        couponDataSource ?: run {
            val couponService = NetworkModule.provideCouponService()
            CouponDataSourceImpl(couponService).also { couponDataSource = it }
        }

    fun provideOrderDataSource(): OrderDataSource =
        orderDataSource ?: run {
            val orderService = NetworkModule.provideOrderService()
            OrderDataSourceImpl(orderService).also { orderDataSource = it }
        }

    fun provideRecentProductDataSource(): RecentProductDataSource =
        recentProductDataSource ?: run {
            val recentProductDao = DatabaseModule.provideRecentProductDao()
            RecentProductDataSourceImpl(recentProductDao).also { recentProductDataSource = it }
        }
}
