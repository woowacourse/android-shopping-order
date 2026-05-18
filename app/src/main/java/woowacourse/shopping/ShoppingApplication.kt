package woowacourse.shopping

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.Credentials
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import woowacourse.shopping.data.local.RecentProductDatabase
import woowacourse.shopping.data.network.cart.CartRetrofitDaoImpl
import woowacourse.shopping.data.network.cart.RetrofitCartService
import woowacourse.shopping.data.network.order.OrderService
import woowacourse.shopping.data.network.product.ProductRetrofitDaoImpl
import woowacourse.shopping.data.network.product.RetrofitProductService
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
import woowacourse.shopping.data.source.auth.AuthDataSourceImpl
import woowacourse.shopping.data.source.order.OrderDaoImpl
import woowacourse.shopping.data.source.product.ProductDataSourceImpl

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

data class AppDependencies(
    val productRepository: ProductRepository,
    val cartRepository: CartRepository,
    val recentProductRepository: RecentProductRepository,
    val orderRepository: OrderRepository,
)

open class ShoppingApplication : Application() {
    private val applicationScope = CoroutineScope(context = SupervisorJob() + Dispatchers.IO)

    lateinit var appDependenciesDeferred: Deferred<AppDependencies>

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            appDependenciesDeferred =
                async {
                    startMockWebServer()
                    initDependencies()
                }
        }
    }

    private suspend fun initDependencies(): AppDependencies {
        val auth: AuthRepository =
            AuthRepositoryImpl(
                dataSource =
                    AuthDataSourceImpl(
                        dataStore = applicationContext.dataStore,
                    ),
            )

        var token = auth.load()
        if (token.isBlank()) {
            token = Credentials.basic("CommitTheKermit", "password")
            auth.save(token)
        }

        val json = Json { ignoreUnknownKeys = true }
        val baseUrl =
            "http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com/".toHttpUrl()

        val client =
            OkHttpClient
                .Builder()
                .addInterceptor { chain ->
                    val authorized =
                        chain
                            .request()
                            .newBuilder()
                            .header("Authorization", token)
                            .build()
                    chain.proceed(authorized)
                }.build()

        val retrofit =
            Retrofit
                .Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .client(client)
                .build()

        val retrofitProductService = retrofit.create(RetrofitProductService::class.java)
        val retrofitCartService = retrofit.create(RetrofitCartService::class.java)
        val orderService = retrofit.create(OrderService::class.java)

        val recentProductDatabase =
            Room
                .databaseBuilder(
                    applicationContext,
                    RecentProductDatabase::class.java,
                    "recent_product.db",
                ).build()

        val product: ProductRepository =
            ProductRepositoryImpl(
                dataSource =
                    ProductDataSourceImpl(
                        productDao =
                            ProductRetrofitDaoImpl(
                                retrofitProductService = retrofitProductService,
                            ),
                    ),
            )

        val cart: CartRepository =
            CartRepositoryImpl(
                cartServerDao =
                    CartRetrofitDaoImpl(
                        retrofitCartService = retrofitCartService,
                    ),
            )
        val recent: RecentProductRepository =
            RecentProductRepositoryImpl(
                recentProductDao = recentProductDatabase.recentProductDao(),
            )

        val order: OrderRepository =
            OrderRepositoryImpl(
                orderDao =
                    OrderDaoImpl(
                        orderService = orderService,
                    ),
            )

        return AppDependencies(
            productRepository = product,
            cartRepository = cart,
            recentProductRepository = recent,
            orderRepository = order,
        )
    }
}
