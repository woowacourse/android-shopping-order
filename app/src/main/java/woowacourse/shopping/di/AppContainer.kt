package woowacourse.shopping.di

import android.content.Context
import androidx.room.Room
import woowacourse.shopping._archive.local.Database
import woowacourse.shopping.network.RetrofitClient
import woowacourse.shopping.network.auth.BasicAuthEncoder
import woowacourse.shopping.repository.CartRepository
import woowacourse.shopping.repository.ProductRepository
import woowacourse.shopping.repository.RecentProductRepository
import woowacourse.shopping.repository.network.RetrofitCartRepository
import woowacourse.shopping.repository.network.RetrofitProductRepository
import woowacourse.shopping.repository.room.RoomRecentProductRepository

object AppContainer {
    private lateinit var database: Database
    val networkClient = RetrofitClient
    val productRepository: ProductRepository =
        RetrofitProductRepository(networkClient.productService)
    val cartRepository: CartRepository = RetrofitCartRepository(
        encoder = BasicAuthEncoder,
        service = networkClient.cartService
    )
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
