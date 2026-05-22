package woowacourse.shopping

import android.content.Context
import kotlinx.serialization.json.Json
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import woowacourse.shopping.data.localdb.ShoppingDB
import woowacourse.shopping.data.remote.NetworkManager
import woowacourse.shopping.data.remote.NetworkObserver
import woowacourse.shopping.data.remote.api.CartApi
import woowacourse.shopping.data.remote.api.ProductApi
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.ProductRepositoryImpl
import woowacourse.shopping.data.repository.RecentItemRepository
import woowacourse.shopping.data.repository.RecentItemRepositoryImpl

class AppContainer(
    private val context: Context,
) {
    private val database = ShoppingDB.getInstance(context)

    val productRepository: ProductRepository by lazy {
        ProductRepositoryImpl(
            api = retrofitService.create(ProductApi::class.java),
        )
    }

    val cartRepository: CartRepository by lazy {
        CartRepositoryImpl(
            api = retrofitService.create(CartApi::class.java),
        )
    }

    val recentItemRepository: RecentItemRepository by lazy {
        RecentItemRepositoryImpl(database.recentItemDao(), productRepository)
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
                val credential =
                    Credentials.basic(
                        username = "GiyunKim00",
                        password = "password",
                    )

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
            .baseUrl("http://techcourse-lv2-alb-250216202.ap-northeast-2.elb.amazonaws.com/")
            .addConverterFactory(
                Json.asConverterFactory(
                    "application/json".toMediaType(),
                ),
            ).client(client)
            .build()
}
