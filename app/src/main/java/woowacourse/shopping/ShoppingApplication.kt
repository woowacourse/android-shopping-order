package woowacourse.shopping

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.Credentials
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import woowacourse.shopping.data.local.RecentProductDatabase
import woowacourse.shopping.data.network.cart.CartServerDaoImpl
import woowacourse.shopping.data.network.product.dto.ProductServerDaoImpl
import woowacourse.shopping.data.network.startMockWebServer
import woowacourse.shopping.data.repository.auth.AuthRepository
import woowacourse.shopping.data.repository.auth.AuthRepositoryImpl
import woowacourse.shopping.data.repository.cart.CartRepository
import woowacourse.shopping.data.repository.cart.CartRepositoryImpl
import woowacourse.shopping.data.repository.cart.RecentProductRepositoryImpl
import woowacourse.shopping.data.repository.product.ProductRepository
import woowacourse.shopping.data.repository.product.ProductRepositoryImpl
import woowacourse.shopping.data.repository.recentproduct.RecentProductRepository
import woowacourse.shopping.data.source.auth.AuthDataSourceImpl
import woowacourse.shopping.data.source.product.ProductDataSourceImpl

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ShoppingApplication : Application() {
    lateinit var productRepository: ProductRepository
        private set
    lateinit var cartRepository: CartRepository
        private set
    lateinit var recentProductRepository: RecentProductRepository
        private set

    override fun onCreate() {
        super.onCreate()
        Thread {
            startMockWebServer()
            initDependencies()
        }.apply {
            start()
            join()
        }
    }

    private fun initDependencies() {
        val auth: AuthRepository = AuthRepositoryImpl(
            dataSource = AuthDataSourceImpl(
                dataStore = applicationContext.dataStore,
            ),
        )

        lateinit var token: String
        runBlocking {
            token = auth.load()
            if (token.isBlank()) {
                token = Credentials.basic("CommitTheKermit", "password")
                auth.save(token)
            }
        }

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val authorized = chain.request().newBuilder()
                    .header("Authorization", token)
                    .build()
                chain.proceed(authorized)
            }
            .build()
        val json = Json { ignoreUnknownKeys = true }
        val baseUrl =
            "http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com/".toHttpUrl()

        val recentProductDatabase = Room.databaseBuilder(
            applicationContext,
            RecentProductDatabase::class.java,
            "recent_product.db",
        ).build()

        val product: ProductRepository = ProductRepositoryImpl(
            dataSource = ProductDataSourceImpl(
                productDao = ProductServerDaoImpl(
                    client = client,
                    baseUrl = baseUrl,
                    json = json,
                ),
            ),
        )
        val cart: CartRepository = CartRepositoryImpl(
            cartServerDao = CartServerDaoImpl(
                client = client,
                baseUrl = baseUrl,
                json = json,
            ),
            productRepository = product,
        )
        val recent: RecentProductRepository =
            RecentProductRepositoryImpl(recentProductDatabase.recentProductDao())

        productRepository = product
        cartRepository = cart
        recentProductRepository = recent
    }
}
