package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.data.ShoppingCartDatabase
import woowacourse.shopping.data.TokenProvider
import woowacourse.shopping.data.datasource.local.RecentProductLocalDataSource
import woowacourse.shopping.data.datasource.remote.CartProductRemoteDataSource
import woowacourse.shopping.data.datasource.remote.ProductRemoteDataSource
import woowacourse.shopping.data.network.RetrofitInstance
import woowacourse.shopping.data.repository.CartProductRepositoryImpl
import woowacourse.shopping.data.repository.ProductRepositoryImpl
import woowacourse.shopping.data.repository.RecentProductRepositoryImpl

class ShoppingApplication : Application() {
    private val database by lazy { ShoppingCartDatabase.getDataBase(this) }
    private val retrofitInstance by lazy { RetrofitInstance(TokenProvider(this)) }

    val productRepository
        by lazy { ProductRepositoryImpl(ProductRemoteDataSource(retrofitInstance.productService)) }
    val cartProductRepository
        by lazy {
            CartProductRepositoryImpl(
                CartProductRemoteDataSource(retrofitInstance.cartProductService),
            )
        }
    val recentProductRepository
        by lazy {
            RecentProductRepositoryImpl(
                RecentProductLocalDataSource(database.recentProductDao),
                productRepository,
            )
        }
}
