package woowacourse.shopping.di

import android.content.Context
import androidx.room.Room
import woowacourse.shopping.data.repository.DefaultCartRepository
import woowacourse.shopping.data.repository.LocalRecentProductRepository
import woowacourse.shopping.data.repository.RemoteProductRepository
import woowacourse.shopping.data.source.local.ShoppingDatabase
import woowacourse.shopping.data.source.remote.CartRemoteDataSource
import woowacourse.shopping.domain.repository.AuthRepository
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository

object RepositoryProvider {
    private lateinit var database: ShoppingDatabase

    val productRepository: ProductRepository = RemoteProductRepository()
    val cartRepository: CartRepository by lazy {
        DefaultCartRepository(
            cartDao = database.cartDao(),
            remoteDataSource = CartRemoteDataSource(),
        )
    }

    val recentProductRepository: RecentProductRepository by lazy {
        LocalRecentProductRepository(
            recentProductDao = database.recentProductDao(),
            productRepository = productRepository,
        )
    }

    val authRepository: AuthRepository = AuthRepository()

    fun init(context: Context) {
        database =
            Room
                .databaseBuilder(
                    context.applicationContext,
                    ShoppingDatabase::class.java,
                    "cart-db",
                ).build()
    }
}
