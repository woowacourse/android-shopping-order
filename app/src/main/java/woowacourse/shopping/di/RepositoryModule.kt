package woowacourse.shopping.di

import android.content.Context
import woowacourse.shopping.data.repository.local.RecentProductRepositoryImpl
import woowacourse.shopping.data.repository.remote.CartRepositoryImpl
import woowacourse.shopping.data.repository.remote.ProductRepositoryImpl
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository

object RepositoryModule {
    private var productRepository: ProductRepository? = null
    private var cartRepository: CartRepository? = null
    private var recentProductRepository: RecentProductRepository? = null
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun provideProductRepository(): ProductRepository =
        productRepository ?: run {
            val productDataSource = DataSourceModule.provideProductDataSource()
            val cartRepository = provideCartRepository()
            ProductRepositoryImpl(productDataSource, cartRepository).also {
                productRepository = it
            }
        }

    fun provideCartRepository(): CartRepository =
        cartRepository ?: run {
            val cartDataSource = DataSourceModule.provideCartDataSource()
            CartRepositoryImpl(cartDataSource).also { cartRepository = it }
        }

    fun provideRecentProductRepository(): RecentProductRepository =
        recentProductRepository ?: run {
            val recentlyProductDataSource = DataSourceModule.provideRecentProductDataSource()
            RecentProductRepositoryImpl(recentlyProductDataSource).also {
                recentProductRepository = it
            }
        }
}
