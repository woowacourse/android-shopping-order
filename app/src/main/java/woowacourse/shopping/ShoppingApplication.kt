package woowacourse.shopping

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import kotlin.jvm.java
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
import woowacourse.shopping.data.network.order.OrderService
import woowacourse.shopping.data.network.product.ProductService
import woowacourse.shopping.data.network.startMockWebServer
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
            dataSource = AuthRemoteDataSource(
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

        val json = Json { ignoreUnknownKeys = true }
        val baseUrl =
            "http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com/".toHttpUrl()

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val authorized = chain.request().newBuilder()
                    .header("Authorization", token)
                    .build()
                chain.proceed(authorized)
            }
            .build()

        val retrofitService = Retrofit.Builder()
            .baseUrl("http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(client)
            .build()
            .create(ProductService::class.java)

        val recentProductDatabase = Room.databaseBuilder(
            applicationContext,
            RecentProductDatabase::class.java,
            "recent_product.db",
        ).build()

        val product: ProductRepository = ProductRepositoryImpl(
            dataSource = ProductRemoteDataSource(
                productDataSource = ProductRemoteDataSource(
                    productService = retrofitService,
                ),
            ),
        )

        val cartService = Retrofit.Builder()
            .baseUrl("http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(client)
            .build()
            .create(CartService::class.java)

        val cart: CartRepository = CartRepositoryImpl(
            cartDataSource = CartRemoteDataSource(
                cartService = cartService,
            ),
            productRepository = product,
        )
        val recent: RecentProductRepository =
            RecentProductRepositoryImpl(recentProductDatabase.recentProductDao())

        val orderService = Retrofit.Builder()
            .baseUrl("http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(client)
            .build()
            .create(OrderService::class.java)

        val order: OrderRepository = OrderRepositoryImpl(
            orderDataSource = OrderRemoteDataSource(
                orderService = orderService,
            ),
        )

        productRepository = product
        cartRepository = cart
        recentProductRepository = recent
        orderRepository = order
    }
}
