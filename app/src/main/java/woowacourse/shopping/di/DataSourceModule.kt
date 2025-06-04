package woowacourse.shopping.di

import android.content.Context
import woowacourse.shopping.data.datasource.local.RecentProductLocalDataSource
import woowacourse.shopping.data.datasource.local.RecentProductLocalDataSourceImpl
import woowacourse.shopping.data.datasource.remote.CartRemoteDataSource
import woowacourse.shopping.data.datasource.remote.CartRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.remote.CouponRemoteDataSource
import woowacourse.shopping.data.datasource.remote.CouponRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.remote.ProductRemoteDataSource
import woowacourse.shopping.data.datasource.remote.ProductRemoteDataSourceImpl

object DataSourceModule {
    private lateinit var appContext: Context
    private const val ERROR_APP_CONTEXT_NOT_INITIALIZE = "appContext가 초기화되지 않았습니다."

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    val cartRemoteDataSource: CartRemoteDataSource by lazy {
        CartRemoteDataSourceImpl(NetworkModule.cartItemService)
    }

    val productRemoteDataSource: ProductRemoteDataSource by lazy {
        ProductRemoteDataSourceImpl(NetworkModule.productService)
    }

    val recentProductLocalDataSource: RecentProductLocalDataSource by lazy {
        check(::appContext.isInitialized) { ERROR_APP_CONTEXT_NOT_INITIALIZE }
        val dao = DatabaseModule.provideRecentProductDao()
        RecentProductLocalDataSourceImpl(dao)
    }

    val couponRemoteDataSource: CouponRemoteDataSource by lazy {
        CouponRemoteDataSourceImpl(NetworkModule.couponService)
    }
}
