package woowacourse.shopping.di

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.runBlocking
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.DefaultCartRepository
import woowacourse.shopping.data.repository.DefaultOrderRepository
import woowacourse.shopping.data.repository.DefaultProductRepository
import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.source.local.ShoppingDatabase
import woowacourse.shopping.data.source.local.auth.AuthDataSource
import woowacourse.shopping.data.source.local.auth.CryptoManager
import woowacourse.shopping.data.source.local.auth.DefaultAuthDataSource
import woowacourse.shopping.data.source.local.notification.NotificationDataSource
import woowacourse.shopping.data.source.remote.CartRemoteDataSource
import woowacourse.shopping.data.source.remote.ProductRemoteDataSource
import woowacourse.shopping.data.source.remote.api.AuthInterceptor
import woowacourse.shopping.data.source.remote.api.RetrofitServices

object RepositoryProvider {
    private lateinit var appContext: Context
    private lateinit var database: ShoppingDatabase

    private lateinit var retrofitServices: RetrofitServices

    lateinit var notificationDataSource: NotificationDataSource

    fun init(
        context: Context,
        id: String,
        password: String,
    ) {
        appContext = context.applicationContext
        database =
            Room
                .databaseBuilder(
                    context.applicationContext,
                    ShoppingDatabase::class.java,
                    "cart-db",
                ).build()

        runBlocking {
            authDataSource.saveToken(id, password)
            notificationDataSource =
                NotificationDataSource(
                    appContext.getSharedPreferences("notification", Context.MODE_PRIVATE),
                )
            retrofitServices =
                RetrofitServices(
                    baseUrl = "http://techcourse-lv2-alb-250216202.ap-northeast-2.elb.amazonaws.com/",
                    interceptor = AuthInterceptor(authDataSource.getToken()),
                )
        }
    }

    val productRepository: ProductRepository by lazy {
        DefaultProductRepository(
            ProductRemoteDataSource(retrofitServices.productService),
            database.recentProductDao(),
        )
    }

    val authDataSource: AuthDataSource by lazy {
        DefaultAuthDataSource(appContext, CryptoManager())
    }

    val cartRepository: CartRepository by lazy {
        DefaultCartRepository(
            remoteDataSource =
                CartRemoteDataSource(
                    cartService = retrofitServices.cartService,
                ),
        )
    }

    val orderRepository: OrderRepository by lazy {
        DefaultOrderRepository(
            remoteOrderDataSource = retrofitServices.couponService,
        )
    }
}
