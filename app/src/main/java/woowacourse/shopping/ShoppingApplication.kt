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
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import woowacourse.shopping.data.datasource.auth.AuthRemoteDataSource
import woowacourse.shopping.data.datasource.cart.CartRemoteDataSource
import woowacourse.shopping.data.datasource.order.OrderRemoteDataSource
import woowacourse.shopping.data.datasource.product.ProductRemoteDataSource
import woowacourse.shopping.data.local.RecentProductDatabase
import woowacourse.shopping.data.network.cart.CartService
import woowacourse.shopping.data.network.mock.ShoppingMockServer
import woowacourse.shopping.data.network.order.OrderService
import woowacourse.shopping.data.network.product.ProductService
import woowacourse.shopping.data.repository.auth.AuthRepository
import woowacourse.shopping.data.repository.auth.AuthRepositoryImpl
import woowacourse.shopping.data.repository.cart.CartRepository
import woowacourse.shopping.data.repository.cart.CartRepositoryImpl
import woowacourse.shopping.data.repository.cart.RecentProductRepositoryImpl
import woowacourse.shopping.data.repository.order.OrderRepository
import woowacourse.shopping.data.repository.order.OrderRepositoryImpl
import woowacourse.shopping.data.repository.product.ProductRepository
import woowacourse.shopping.data.repository.product.ProductRepositoryImpl
import woowacourse.shopping.data.repository.recentproduct.RecentProductRepository

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ShoppingApplication : Application() {
    lateinit var productRepository: ProductRepository
        private set
    lateinit var cartRepository: CartRepository
        private set
    lateinit var recentProductRepository: RecentProductRepository
        private set
    lateinit var orderRepository: OrderRepository
        private set
    private var mockServer: ShoppingMockServer? = null

    override fun onCreate() {
        super.onCreate()
        initDependencies()
    }

    override fun onTerminate() {
        mockServer?.shutdown()
        super.onTerminate()
    }

    private fun initDependencies() {
        val auth: AuthRepository = AuthRepositoryImpl(
            dataSource = AuthRemoteDataSource(
                dataStore = applicationContext.dataStore,
            ),
            issueToken = ::issueBasicCredential,
        )

        val json = Json { ignoreUnknownKeys = true }
        val baseUrl = if (BuildConfig.SHOPPING_USE_MOCK_SERVER || BuildConfig.SHOPPING_BASE_URL.isBlank()) {
            ShoppingMockServer(json)
                .also { mockServer = it }
                .start()
        } else {
            BuildConfig.SHOPPING_BASE_URL.toHttpUrl()
        }

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = runBlocking { auth.token() }
                val request = chain.request().newBuilder().apply {
                    if (token.isNotBlank()) header("Authorization", token)
                }.build()
                chain.proceed(request)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(client)
            .build()

        val productService = retrofit.create(ProductService::class.java)
        val cartService = retrofit.create(CartService::class.java)
        val orderService = retrofit.create(OrderService::class.java)

        val recentProductDatabase = Room.databaseBuilder(
            applicationContext,
            RecentProductDatabase::class.java,
            "recent_product.db",
        ).build()

        val product: ProductRepository = ProductRepositoryImpl(
            dataSource = ProductRemoteDataSource(productService = productService),
        )
        val cart: CartRepository = CartRepositoryImpl(
            cartDataSource = CartRemoteDataSource(cartService = cartService),
            productRepository = product,
        )
        val recent: RecentProductRepository =
            RecentProductRepositoryImpl(recentProductDatabase.recentProductDao())
        val order: OrderRepository = OrderRepositoryImpl(
            orderDataSource = OrderRemoteDataSource(orderService = orderService),
        )

        productRepository = product
        cartRepository = cart
        recentProductRepository = recent
        orderRepository = order
    }

    private fun issueBasicCredential(): String? {
        val user = BuildConfig.SHOPPING_USERNAME
        val pw = BuildConfig.SHOPPING_PASSWORD
        if (user.isBlank() || pw.isBlank()) return null
        return Credentials.basic(user, pw)
    }
}
