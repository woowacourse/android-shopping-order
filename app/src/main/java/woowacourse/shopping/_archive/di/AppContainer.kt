package woowacourse.shopping._archive.di

import android.content.Context
import androidx.room.Room
import woowacourse.shopping._archive.local.Database
import woowacourse.shopping._archive.network.NetworkClient
import woowacourse.shopping.network.RetrofitClient
import woowacourse.shopping.network.service.ProductService
import woowacourse.shopping.repository.CartRepository
import woowacourse.shopping.repository.ProductRepository
import woowacourse.shopping.repository.RecentProductRepository
import woowacourse.shopping.repository.network.LegacyNetworkProductRepository
import woowacourse.shopping.repository.network.RetrofitRepository
import woowacourse.shopping.repository.room.RoomCartRepository
import woowacourse.shopping.repository.room.RoomRecentProductRepository

object AppContainer {
    private lateinit var database: Database

    val networkClient = NetworkClient()
    val productRepository: ProductRepository =
        RetrofitRepository(RetrofitClient.productService)
    val cartRepository: CartRepository by lazy {
        RoomCartRepository(
            cartDao = database.cartDao(),
            productRepository = productRepository,
        )
    }
    val recentProductRepository: RecentProductRepository by lazy {
        RoomRecentProductRepository(
            recentProductDao = database.recentProductDao(),
            productRepository = productRepository,
        )
    }

    fun init(context: Context) {
        database =
            Room
                .databaseBuilder(
                    context.applicationContext,
                    Database::class.java,
                    "shopping-db",
                ).fallbackToDestructiveMigration(false)
                .build()
    }
}
