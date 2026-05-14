package woowacourse.shopping.di

import android.content.Context
import androidx.room.Room
import woowacourse.shopping.data.local.Database
import woowacourse.shopping.data.remote.RetrofitClient
import woowacourse.shopping.data.remote.auth.BasicAuthEncoder
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RecentProductRepository
import woowacourse.shopping.data.repository.network.RetrofitCartRepository
import woowacourse.shopping.data.repository.network.RetrofitOrderRepository
import woowacourse.shopping.data.repository.network.RetrofitProductRepository
import woowacourse.shopping.data.repository.room.RoomRecentProductRepository

class AppContainer(context: Context) {
    private val database: Database = Room
        .databaseBuilder(
            context.applicationContext,
            Database::class.java,
            "shopping-db",
        ).fallbackToDestructiveMigration(false)
        .build()
    private val networkClient = RetrofitClient
    private val encoder = BasicAuthEncoder

    val productRepository: ProductRepository =
        RetrofitProductRepository(networkClient.productService)
    val cartRepository: CartRepository =
        RetrofitCartRepository(
            encoder = encoder,
            service = networkClient.cartService
        )
    val recentProductRepository: RecentProductRepository by lazy {
        RoomRecentProductRepository(
            recentProductDao = database.recentProductDao(),
            productRepository = productRepository,
        )
    }
    val orderRepository: OrderRepository =
        RetrofitOrderRepository(
            encoder = encoder,
            service = networkClient.orderService
        )
}
