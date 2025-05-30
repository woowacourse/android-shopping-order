package woowacourse.shopping.di

import android.content.Context
import woowacourse.shopping.data.datasource.local.RecentProductLocalDataSource
import woowacourse.shopping.data.datasource.local.RecentProductLocalDataSourceImpl
import woowacourse.shopping.data.datasource.remote.CartRemoteDataSource
import woowacourse.shopping.data.datasource.remote.CartRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.remote.ProductRemoteDataSource
import woowacourse.shopping.data.datasource.remote.ProductRemoteDataSourceImpl

object DataSourceModule {
    private var cartRemoteDataSource: CartRemoteDataSource? = null
    private var productRemoteDataSource: ProductRemoteDataSource? = null
    private var recentProductLocalDataSource: RecentProductLocalDataSource? = null
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun provideProductDataSource(): ProductRemoteDataSource =
        productRemoteDataSource ?: run {
            val productService = NetworkModule.provideProductService()
            ProductRemoteDataSourceImpl(productService).also { productRemoteDataSource = it }
        }

    fun provideCartDataSource(): CartRemoteDataSource =
        cartRemoteDataSource ?: run {
            val cartItemService = NetworkModule.provideCartItemService()
            CartRemoteDataSourceImpl(cartItemService).also { cartRemoteDataSource = it }
        }

    fun provideRecentProductDataSource(): RecentProductLocalDataSource =
        recentProductLocalDataSource ?: run {
            val recentProductDao = DatabaseModule.provideRecentProductDao()
            RecentProductLocalDataSourceImpl(recentProductDao).also {
                recentProductLocalDataSource = it
            }
        }
}
