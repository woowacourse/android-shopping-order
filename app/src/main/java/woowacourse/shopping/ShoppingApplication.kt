package woowacourse.shopping

import android.app.Application
import android.content.SharedPreferences
import androidx.room.Room
import woowacourse.shopping.data.authentication.repository.DefaultAuthenticationRepository
import woowacourse.shopping.data.network.ApiClient
import woowacourse.shopping.data.product.local.database.ProductDatabase
import woowacourse.shopping.data.product.remote.service.ProductService
import woowacourse.shopping.data.product.repository.DefaultProductsRepository
import woowacourse.shopping.data.shoppingCart.remote.service.ShoppingCartService
import woowacourse.shopping.data.shoppingCart.repository.DefaultShoppingCartRepository
import woowacourse.shopping.domain.authentication.UserAuthentication
import kotlin.concurrent.thread

class ShoppingApplication : Application() {
    lateinit var productDatabase: ProductDatabase

    private val authDataSource: SharedPreferences by lazy {
        getSharedPreferences(
            "authentication",
            MODE_PRIVATE,
        )
    }

    private val productService: ProductService by lazy {
        ApiClient
            .getApiClient(DefaultAuthenticationRepository.get())
            .create(ProductService::class.java)
    }
    private val shoppingCartService: ShoppingCartService by lazy {
        ApiClient
            .getApiClient(DefaultAuthenticationRepository.get())
            .create(ShoppingCartService::class.java)
    }

    override fun onCreate() {
        super.onCreate()
        productDatabase =
            Room
                .databaseBuilder(
                    applicationContext,
                    ProductDatabase::class.java,
                    "recent_watching",
                ).fallbackToDestructiveMigration()
                .build()
        DefaultAuthenticationRepository.initialize(authDataSource)

        DefaultShoppingCartRepository.initialize(shoppingCartService)
        DefaultProductsRepository.initialize(
            productDatabase.recentWatchingDao(),
            productService,
        )

        thread {
            DefaultAuthenticationRepository.get().updateUserAuthentication(
                UserAuthentication(
                    id = "oungsi2000",
                    password = "password",
                ),
            )
        }.join()
    }
}
