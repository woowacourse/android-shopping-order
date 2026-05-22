package woowacourse.shopping.di

import android.content.Context
import androidx.room.Room
import woowacourse.shopping.data.repository.DefaultCartRepository
import woowacourse.shopping.data.repository.DefaultProductRepository
import woowacourse.shopping.data.repository.DefaultRecentProductRepository
import woowacourse.shopping.data.source.local.ShoppingDatabase
import woowacourse.shopping.data.source.local.auth.AuthDataSource
import woowacourse.shopping.data.source.local.auth.DefaultAuthDataSource
import woowacourse.shopping.data.source.remote.RetrofitClient
import woowacourse.shopping.data.source.remote.datasource.CartRemoteDataSource
import woowacourse.shopping.data.source.remote.datasource.ProductRemoteDataSource
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository

object RepositoryProvider {
    private lateinit var appContext: Context
    private lateinit var database: ShoppingDatabase

    fun init(context: Context) {
        appContext = context.applicationContext
        database =
            Room
                .databaseBuilder(
                    context.applicationContext,
                    ShoppingDatabase::class.java,
                    "cart-db",
                ).build()
    }

    val authDataSource: AuthDataSource by lazy {
        DefaultAuthDataSource(appContext)
    }

    private val retrofitClient: RetrofitClient by lazy {
        RetrofitClient(authDataSource = authDataSource)
    }

    val productRepository: ProductRepository by lazy {
        DefaultProductRepository(
            remoteDataSource = ProductRemoteDataSource(retrofitClient.retrofit),
        )
    }

    val recentProductRepository: RecentProductRepository by lazy {
        DefaultRecentProductRepository(
            recentProductDao = database.recentProductDao(),
            productRepository = productRepository,
        )
    }

    val cartRepository: CartRepository by lazy {
        DefaultCartRepository(
            remoteDataSource = CartRemoteDataSource(retrofitClient.retrofit),
        )
    }
}
