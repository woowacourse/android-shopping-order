package woowacourse.shopping

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
import woowacourse.shopping.data.remote.api.ProductApi
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.data.repository.CouponRepository
import woowacourse.shopping.data.repository.CouponRepositoryImpl
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.ProductRepositoryImpl
import woowacourse.shopping.data.repository.RecentItemRepository

class AppContainer(
    context: Context,
) {
    private val applicationContext = context.applicationContext
    private val database = ShoppingDB.getInstance(applicationContext)

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val userDataStore = UserDataStore(applicationContext)

    init {
        applicationScope.launch {
            userDataStore.saveUser(
                username = "byunghyunkim0",
                password = "password",
            )
        }
    }

    val productRepository: ProductRepository by lazy {
        ProductRepositoryImpl(
            api = retrofitService.create(ProductApi::class.java),
        )
    }

    val cartRepository: CartRepository by lazy {
        CartRepositoryImpl(api = retrofitService.create(CartApi::class.java))
    }

    val couponRepository: CouponRepository by lazy {
        CouponRepositoryImpl(api = retrofitService.create(CouponApi::class.java))
    }

    val recentItemRepository: RecentItemRepository by lazy {
        RecentItemRepository(database.recentItemDao())
    }

    val networkObserver: NetworkObserver by lazy {
        NetworkManager(applicationContext)
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
                val credential =
                    runBlocking {
                        val username = userDataStore.username.first()
                        val password = userDataStore.password.first()

                        Credentials.basic(username, password)
                    }
                val request =
                    chain
                        .request()
                        .newBuilder()
                        .header(
                            "Authorization",
                            credential,
                        ).build()

                chain.proceed(request)
            }.addInterceptor(loggingInterceptor)
            .build()

    val retrofitService =
        Retrofit
            .Builder()
            .baseUrl("http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com/")
            .addConverterFactory(
                Json.asConverterFactory(
                    "application/json".toMediaType(),
                ),
            ).client(client)
            .build()
}
