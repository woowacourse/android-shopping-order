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
import okhttp3.Credentials
import woowacourse.shopping.data.local.RecentProductDatabase
import woowacourse.shopping.data.network.RetrofitClient
import woowacourse.shopping.data.network.cart.CartRetrofitDaoImpl
import woowacourse.shopping.data.network.coupon.CouponDaoImpl
import woowacourse.shopping.data.network.product.ProductRetrofitDaoImpl
import woowacourse.shopping.data.network.startMockWebServer
import woowacourse.shopping.data.repository.auth.AuthRepository
import woowacourse.shopping.data.repository.auth.AuthRepositoryImpl
import woowacourse.shopping.data.repository.cart.CartRepository
import woowacourse.shopping.data.repository.cart.CartRepositoryImpl
import woowacourse.shopping.data.repository.coupon.CouponRepository
import woowacourse.shopping.data.repository.coupon.CouponRepositoryImpl
import woowacourse.shopping.data.repository.order.OrderRepository
import woowacourse.shopping.data.repository.order.OrderRepositoryImpl
import woowacourse.shopping.data.repository.product.ProductRepository
import woowacourse.shopping.data.repository.product.ProductRepositoryImpl
import woowacourse.shopping.data.repository.recentproduct.RecentProductRepository
import woowacourse.shopping.data.repository.recentproduct.RecentProductRepositoryImpl
import woowacourse.shopping.data.source.auth.AuthDataSourceImpl
import woowacourse.shopping.data.source.coupon.CouponDataSourceImpl
import woowacourse.shopping.data.source.order.OrderDaoImpl
import woowacourse.shopping.data.source.product.ProductDataSourceImpl

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

data class AppDependencies(
    val productRepository: ProductRepository,
    val cartRepository: CartRepository,
    val recentProductRepository: RecentProductRepository,
    val couponRepository: CouponRepository,
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

        RetrofitClient.setToken(token)

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
                                retrofitProductService = RetrofitClient.productService,
                            ),
                    ),
            )

        val cart: CartRepository =
            CartRepositoryImpl(
                cartServerDao =
                    CartRetrofitDaoImpl(
                        retrofitCartService = RetrofitClient.cartService,
                    ),
            )
        val recent: RecentProductRepository =
            RecentProductRepositoryImpl(
                recentProductDao = recentProductDatabase.recentProductDao(),
            )

        val coupon: CouponRepository =
            CouponRepositoryImpl(
                dataSource =
                    CouponDataSourceImpl(
                        couponDao =
                            CouponDaoImpl(
                                couponService = RetrofitClient.couponService,
                            ),
                    ),
            )


        val order: OrderRepository =
            OrderRepositoryImpl(
                orderDao =
                    OrderDaoImpl(
                        orderService = RetrofitClient.orderService,
                    ),
            )

        return AppDependencies(
            productRepository = product,
            cartRepository = cart,
            recentProductRepository = recent,
            couponRepository = coupon,
            orderRepository = order,
        )
    }
}
