package woowacourse.shopping

import android.app.Application
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.local.database.RecentProductDatabase
import woowacourse.shopping.data.local.preferences.ShoppingPreferencesManager
import woowacourse.shopping.data.remote.BasicAuthInterceptor
import woowacourse.shopping.data.remote.CartService
import woowacourse.shopping.data.remote.OrderService
import woowacourse.shopping.data.remote.ProductService
import woowacourse.shopping.data.datasource.DefaultRemoteCartDataSource
import woowacourse.shopping.data.datasource.DefaultRemoteOrderDataSource
import woowacourse.shopping.data.datasource.DefaultRemoteProductDataSource

class ShoppingApplication : Application() {
    private val shoppingPreferencesManager: ShoppingPreferencesManager by lazy {
        ShoppingPreferencesManager(this)
    }

    override fun onCreate() {
        super.onCreate()
        val username = when (val name =
            shoppingPreferencesManager.getString(ShoppingPreferencesManager.KEY_USERNAME)) {
            null -> shoppingPreferencesManager.setString(
                ShoppingPreferencesManager.KEY_USERNAME,
                BuildConfig.USER_NAME
            )

            else -> name
        }

        val password = when (val pwd =
            shoppingPreferencesManager.getString(ShoppingPreferencesManager.KEY_PASSWORD)) {
            null ->
                shoppingPreferencesManager.setString(
                    ShoppingPreferencesManager.KEY_PASSWORD,
                    BuildConfig.PASSWORD
                )

            else -> pwd
        }

        val client =
            OkHttpClient.Builder()
                .addInterceptor(BasicAuthInterceptor(username = username, password = password))
                .addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                )
                .build()

        val retrofit =
            Retrofit.Builder().baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

        val productService = retrofit.create(ProductService::class.java)
        val cartService = retrofit.create(CartService::class.java)
        val orderService = retrofit.create(OrderService::class.java)
        remoteCartDataSource = DefaultRemoteCartDataSource(cartService)
        remoteProductDataSource = DefaultRemoteProductDataSource(productService)
        remoteOrderDataSource = DefaultRemoteOrderDataSource(orderService)
        recentProductDatabase = RecentProductDatabase.getInstance(this)
    }

    companion object {
        lateinit var shoppingPreferencesManager: ShoppingPreferencesManager
        lateinit var recentProductDatabase: RecentProductDatabase
        lateinit var remoteCartDataSource: DefaultRemoteCartDataSource
        lateinit var remoteProductDataSource: DefaultRemoteProductDataSource
        lateinit var remoteOrderDataSource: DefaultRemoteOrderDataSource
    }
}
