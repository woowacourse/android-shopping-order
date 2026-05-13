package woowacourse.shopping

import android.content.Context
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import woowacourse.shopping.data.localdb.ShoppingDB
import woowacourse.shopping.data.remote.HttpProductServer
import woowacourse.shopping.data.remote.NetworkManager
import woowacourse.shopping.data.remote.NetworkObserver
import woowacourse.shopping.data.remote.api.ProductApi
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.HttpProductRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.ProductRepositoryImpl
import woowacourse.shopping.data.repository.RecentItemRepository
import java.util.concurrent.TimeUnit
import kotlin.jvm.java

class AppContainer(
    context: Context,
) {
    private val database = ShoppingDB.getInstance(context)

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient
            .Builder()
            .callTimeout(10L, TimeUnit.SECONDS)
            .connectTimeout(10L, TimeUnit.SECONDS)
            .readTimeout(10L, TimeUnit.SECONDS)
            .writeTimeout(10L, TimeUnit.SECONDS)
            .build()
    }

    val productRepository: ProductRepository by lazy {
        ProductRepositoryImpl(
            api = retrofitService.create(ProductApi::class.java)
        )
    }

    val cartRepository: CartRepository by lazy {
        CartRepository(database.cartItemDao())
    }

    val recentItemRepository: RecentItemRepository by lazy {
        RecentItemRepository(database.recentItemDao(), productRepository)
    }

    val networkObserver: NetworkObserver by lazy {
        NetworkManager(context)
    }

    private val loggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    private val client =
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

    val retrofitService = Retrofit.Builder()
        .baseUrl("http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com/")
        .addConverterFactory(
            Json.asConverterFactory(
                "application/json".toMediaType(),
            )
        )
        .client(client)
        .build()
}
