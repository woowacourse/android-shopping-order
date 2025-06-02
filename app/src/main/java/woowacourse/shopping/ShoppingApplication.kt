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

class ShoppingApplication : Application() {
    private val productDatabase: ProductDatabase by lazy {
        Room
            .databaseBuilder(
                applicationContext,
                ProductDatabase::class.java,
                "recent_watching",
            ).fallbackToDestructiveMigration()
            .build()
    }

    private val authDataSource: SharedPreferences by lazy {
        getSharedPreferences(
            "authentication",
            MODE_PRIVATE,
        )
    }

    private val productService: ProductService by lazy {
        ApiClient
            .getApiClient()
            .create(ProductService::class.java)
    }

    private val shoppingCartService: ShoppingCartService by lazy {
        ApiClient
            .getAuthenticationApiClient(DefaultAuthenticationRepository.get())
            .create(ShoppingCartService::class.java)
    }

    override fun onCreate() {
        super.onCreate()
        DefaultAuthenticationRepository.initialize(authDataSource)
        DefaultShoppingCartRepository.initialize(shoppingCartService)
        DefaultProductsRepository.initialize(
            productDatabase.recentWatchingDao(),
            productService,
        )

        DefaultAuthenticationRepository.get().updateUserAuthentication(
            UserAuthentication(
                id = BuildConfig.USER_ID,
                password = BuildConfig.USER_PASSWORD,
            ),
        )
    }
}
