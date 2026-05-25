package woowacourse.shopping

import android.content.Context
import kotlinx.serialization.json.Json
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import woowacourse.shopping.data.localdata.UserDataStore
import woowacourse.shopping.data.localdb.ShoppingDB
import woowacourse.shopping.data.remote.NetworkManager
import woowacourse.shopping.data.remote.NetworkObserver
import woowacourse.shopping.data.remote.api.CartApi
import woowacourse.shopping.data.remote.api.CouponApi
import woowacourse.shopping.data.remote.api.OrderApi
import woowacourse.shopping.data.remote.api.ProductApi
import woowacourse.shopping.data.repository.AuthRepository
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.data.repository.CouponRepository
import woowacourse.shopping.data.repository.CouponRepositoryImpl
import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.ProductRepositoryImpl
import woowacourse.shopping.data.repository.RecentItemRepository
import woowacourse.shopping.data.repository.RecentItemRepositoryImpl

class AppContainer(
    private val context: Context,
) {
    private val database = ShoppingDB.getInstance(context)

    val userDataStore = UserDataStore(context)

    val authRepository = AuthRepository(userDataStore)

    val productRepository: ProductRepository by lazy {
        ProductRepositoryImpl(
            productApi = retrofitService.create(ProductApi::class.java),
        )
    }

    val cartRepository: CartRepository by lazy {
        CartRepositoryImpl(cartApi = retrofitService.create(CartApi::class.java), cartItemQuantityDao = database.cartItemQuantityDao())
    }

    val couponRepository: CouponRepository by lazy {
        CouponRepositoryImpl(
            couponApi = retrofitService.create(CouponApi::class.java),
        )
    }

    val orderRepository: OrderRepository by lazy {
        OrderRepositoryImpl(
            orderApi = retrofitService.create(OrderApi::class.java),
        )
    }

    val recentItemRepository: RecentItemRepository by lazy {
        RecentItemRepositoryImpl(database.recentItemDao())
    }

    val networkObserver: NetworkObserver by lazy {
        NetworkManager(context)
    }

    private val loggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
            redactHeader("Authorization")
        }

    private val client =
        OkHttpClient
            .Builder()
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()

                val userName = authRepository.userName.value
                val password = authRepository.password.value
                if (!userName.isNullOrBlank() && !password.isNullOrBlank()) {
                    val credential = Credentials.basic(userName, password)
                    requestBuilder.header("Authorization", credential)
                }
                chain.proceed(requestBuilder.build())
            }.addInterceptor(loggingInterceptor)
            .build()

    val retrofitService: Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(
                Json.asConverterFactory(
                    "application/json".toMediaType(),
                ),
            ).client(client)
            .build()
}
