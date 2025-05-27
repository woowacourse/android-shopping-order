package woowacourse.shopping

import android.app.Application
import android.content.SharedPreferences
import androidx.room.Room
import woowacourse.shopping.data.authentication.repository.DefaultAuthenticationRepository
import woowacourse.shopping.data.product.local.database.ProductDatabase
import woowacourse.shopping.data.product.repository.DefaultProductsRepository
import woowacourse.shopping.data.shoppingCart.local.database.ShoppingCartDatabase
import woowacourse.shopping.data.shoppingCart.repository.DefaultShoppingCartRepository
import woowacourse.shopping.domain.authentication.UserAuthentication

class ShoppingApplication : Application() {
    private val shoppingCartDatabase: ShoppingCartDatabase by lazy {
        Room
            .databaseBuilder(
                applicationContext,
                ShoppingCartDatabase::class.java,
                "shoppingCart",
            ).fallbackToDestructiveMigration()
            .build()
    }

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

    override fun onCreate() {
        super.onCreate()
        DefaultShoppingCartRepository.initialize(shoppingCartDatabase.shoppingCartDao())
        DefaultProductsRepository.initialize(productDatabase.recentWatchingDao())
        DefaultAuthenticationRepository.initialize(authDataSource)

        DefaultAuthenticationRepository.get().updateUserAuthentication(
            UserAuthentication(
                id = "oungsi2000",
                password = "password",
            ),
        )
    }
}
